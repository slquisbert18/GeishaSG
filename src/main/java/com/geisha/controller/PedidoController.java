package com.geisha.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geisha.entity.DetalleTrabajo;
import com.geisha.entity.Pedido;
import com.geisha.entity.Persona;
import com.geisha.entity.Tramite;
import com.geisha.entity.Usuario;
import com.geisha.pdf.PdfService;
import com.geisha.repository.UsuarioRepository;
import com.geisha.security.UsuarioDetails;
import com.geisha.service.DetalleTrabajoService;
import com.geisha.service.PedidoService;
import com.geisha.service.PersonaService;
import com.geisha.service.TramiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PersonaService personaService;
    private final TramiteService tramiteService;
    private final DetalleTrabajoService detalleTrabajoService;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final PdfService pdfService;

    // listar
    @GetMapping("/pedidos")
    public String listar(Model model,
                         @RequestParam(required = false) String buscar,
                         @RequestParam(required = false) String fecha) {

        /*
         * Comportamiento del filtro de fecha:
         * - "fecha" no viene en la URL (primera vez que se abre la
         *   pagina) -> se asume HOY. Asi el listado nace "en blanco" cada
         *   jornada, sin tener que crear nada a mano.
         * - "fecha" viene vacia (el usuario le dio al boton de limpiar) ->
         *   se quita el filtro y se ven los pedidos de todos los dias.
         * - "fecha" viene con un valor -> se filtra exactamente ese dia.
         */
        LocalDate fechaFiltro;
        if (fecha == null) {
            fechaFiltro = LocalDate.now();
        } else if (fecha.isBlank()) {
            fechaFiltro = null;
        } else {
            fechaFiltro = LocalDate.parse(fecha);
        }

        model.addAttribute("pedidos", pedidoService.filtrar(fechaFiltro, buscar));
        model.addAttribute("buscar", buscar);
        model.addAttribute("fecha", fechaFiltro);
        model.addAttribute("modulo", "pedidos");

        return "pedidos/listar";
    }

    // nuevo
    @GetMapping("/pedidos/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("pedido", new Pedido());

        agregarDatosDeApoyo(model);

        // Clientes disponibles
//        model.addAttribute("clientes", personaService.listarClientes());

        // Trámites disponibles
//        model.addAttribute("tramites", tramiteService.listarTodos());

        model.addAttribute("tituloFormulario", "Nuevo pedido");
        model.addAttribute("modulo", "pedidos");

        return "pedidos/formulario";
    }

    // editar
    @GetMapping("/pedidos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Pedido pedido = pedidoService.buscarPorId(id).orElseThrow();

        model.addAttribute("pedido", pedido);

        agregarDatosDeApoyo(model);

//        model.addAttribute("clientes", personaService.listarClientes());
//
//        model.addAttribute("tramites", tramiteService.listarTodos());

        model.addAttribute("tituloFormulario", "Editar pedido");
        model.addAttribute("modulo", "pedidos");

        return "pedidos/formulario";
    }

    /*
     * Arma los datos que el formulario necesita para los combobox de
     * busqueda (cliente y tramite). En vez de pasar las entidades
     * completas (que arrastran relaciones @ManyToOne/@OneToOne perezosas
     * dificiles de convertir a JSON), armamos mapas simples con solo lo
     * que la vista necesita mostrar y filtrar, y los serializamos a JSON
     * aqui mismo para que la plantilla solo tenga que "pegarlos" dentro
     * de un <script> (ver pedidos/formulario.html).
     */
    private void agregarDatosDeApoyo(Model model){
        List<Map<String, Object>> clientes = personaService.listarClientes().stream().
                map(persona -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", persona.getId());
                    item.put("nombre", persona.getNombres() + " " + persona.getApellidos());
                    return item;
                }).toList();

        // solo mostrar tramites activos
        List<Map<String, Object>> tramites = tramiteService.listarTodos().stream()
                .filter(Tramite::getActivo)
                .map(tramite -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", tramite.getId());
                    item.put("nombre", tramite.getNombre());
                    // BigDecimal puede venir null si el tramite no definio precio base
                    item.put("precio", tramite.getPrecioBase() != null ? tramite.getPrecioBase() : BigDecimal.ZERO);
                    return item;
                }).toList();

        model.addAttribute("clientesJson", aJson(clientes));
        model.addAttribute("tramitesJson", aJson(tramites));
    }

    private String aJson(Object valor){
        try{
            return objectMapper.writeValueAsString(valor);
        }
        catch(JsonProcessingException e){
            throw new RuntimeException("No se pudo preparar los datos del formulario", e);
        }
    }

    // guardar
    @PostMapping("/pedidos/guardar")
    public String guardar(@ModelAttribute Pedido pedido,
                          @RequestParam(required = false) List<Long> detalleTramiteId,
                          @RequestParam(required = false) List<BigDecimal> detallePrecio,
                          @RequestParam(required = false) List<String> detalleObservaciones,
                          @RequestParam(required = false) List<String> detalleRuta,
                          @AuthenticationPrincipal UsuarioDetails usuarioDetails,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        boolean esNuevo = pedido.getId() == null;

        Pedido pedidoExistente = esNuevo ? null : pedidoService.buscarPorId(pedido.getId()).orElseThrow();

        if(esNuevo){
            // Spring Security inyecta aqui al usuario que tiene la sesion
            // activa (el que resolvio UsuarioDetailsService en el login).
            // De ahi sacamos la Persona real: asi el pedido queda
            // registrado a nombre de quien realmente esta atendiendo,
            // sin depender de un id fijo ni de que el formulario lo mande.
            pedido.setEmpleado(usuarioDetails.getUsuario().getPersona());
        } else {
            // el formulario de edicion no incluye el campo "empleado", por
            // lo que @ModelAttribute lo deja en null: se recupera el
            // empleado original desde BD para no perder ese dato ni
            // reasignar el pedido a quien lo esta editando ahora.
            pedido.setFechaRegistro(pedidoExistente.getFechaRegistro());
            pedido.setEmpleado(pedidoExistente.getEmpleado());
        }

        // ===== VALIDACION =====
        // en vez de tirar una excepcion generica (que manda a la pagina de
        // error y pierde todo lo que el usuario escribio), juntamos los
        // problemas encontrados y, si hay alguno, volvemos a mostrar el
        // mismo formulario con los datos ya ingresados y el mensaje
        // correspondiente junto a cada campo.
        List<String> errores = new ArrayList<>();
        if(pedido.getCliente() == null){
            errores.add("cliente");
        }

        if(detalleTramiteId == null || detalleTramiteId.isEmpty()){
            errores.add("trabajos:El pedido debe incluir al menos un trabajo");
        }
        else{
            for(int i = 0 ; i < detalleTramiteId.size() ; i++){
                if(detalleTramiteId.get(i) == null){
                    errores.add("trabajos:Seleccione un trámite en todas las filas");
                    break;
                }
                BigDecimal precio = (detallePrecio != null && detallePrecio.size() > i) ? detallePrecio.get(i) : null;
                if(precio == null || precio.compareTo(BigDecimal.ZERO) <= 0){
                    errores.add("trabajos:El precio de cada trabajo debe ser mayor a cero");
                    break;
                }
            }
        }

        if(!errores.isEmpty()){
            return volverAlFormularioConError(pedido, detalleTramiteId, detallePrecio, detalleObservaciones, detalleRuta, errores, model);
        }

        List<DetalleTrabajo> detalles = new ArrayList<>();
        BigDecimal montoTotal = BigDecimal.ZERO;

        for(int i = 0 ; i < detalleTramiteId.size() ; i++){
            Tramite tramite = tramiteService.buscarPorId(detalleTramiteId.get(i))
                    .orElseThrow(() -> new RuntimeException("Trámite no encontrado"));

            BigDecimal precio = detallePrecio.get(i);

            // observaciones y ruta son opcionales: si no se envia nada o
            // la fila no tiene texto, quedan en null
            String observaciones = (detalleObservaciones != null && detalleObservaciones.size() > i)
                    ? detalleObservaciones.get(i)
                    : null;

            String ruta = (detalleRuta != null && detalleRuta.size() > i)
                    ? detalleRuta.get(i)
                    : null;

            DetalleTrabajo detalle = DetalleTrabajo.builder()
                    .tramite(tramite)
                    .precioServicio(precio)
                    .observaciones((observaciones == null || observaciones.isBlank()) ? null : observaciones)
                    .ruta((ruta == null || ruta.isBlank()) ? null : ruta.trim())
                    .pedido(pedido)
                    .build();
            detalles.add(detalle);
            montoTotal = montoTotal.add(precio);
        }

        // el input de monto total en el formulario es solo de lectura
        // (una vista previa calculada en JS); el valor que realmente se
        // guarda se recalcula aqui, sumando los precios de cada trabajo,
        // para que nadie pueda alterar el total manipulando el request
        pedido.setMontoTotal(montoTotal);

        // se reemplaza la lista completa de trabajos del pedido. Gracias a
        // cascade=ALL + orphanRemoval=true en Pedido.detalles, al guardar
        // Hibernate borra los trabajos que ya no esten en esta lista y guarda
        // los nuevos, todo en la misma transaccion.
        pedido.setDetalles(detalles);

        pedidoService.guardar(pedido);

        redirectAttributes.addFlashAttribute("mensajeExito",
                esNuevo
                        ? "Pedido registrado correctamente"
                        : "Pedido actualizado correctamente"
        );

        return "redirect:/pedidos";
    }

    /*
     * Vuelve a mostrar pedidos/formulario con lo que el usuario ya habia
     * llenado (para no obligarlo a escribir todo de nuevo) mas los
     * mensajes de error correspondientes.
     *
     * "errores" mezcla dos formatos simples para no crear una clase extra:
     *  - "cliente"            -> error general del campo cliente
     *  - "trabajos:<mensaje>" -> mensaje de error para la seccion de trabajos
     */
    private String volverAlFormularioConError(Pedido pedido,
                                              List<Long> detalleTramiteId,
                                              List<BigDecimal> detallePrecio,
                                              List<String> detalleObservaciones,
                                              List<String> detalleRuta,
                                              List<String> errores,
                                              Model model) {

        model.addAttribute("errorCliente", errores.contains("cliente"));

        String errorTrabajos = errores.stream()
                .filter(e -> e.startsWith("trabajos:"))
                .map(e -> e.substring("trabajos:".length()))
                .findFirst()
                .orElse(null);
        model.addAttribute("errorTrabajos", errorTrabajos);

        // reconstruye las filas de trabajo ya ingresadas (aunque no se
        // vayan a guardar) para que la vista las vuelva a dibujar con
        // th:each="detalle : ${pedido.detalles}" tal como ya lo hace para
        // la edicion normal, sin necesitar una plantilla aparte
        List<DetalleTrabajo> detalles = new ArrayList<>();
        if (detalleTramiteId != null) {
            for (int i = 0; i < detalleTramiteId.size(); i++) {
                Long tramiteId = detalleTramiteId.get(i);
                Tramite tramite = tramiteId != null ? tramiteService.buscarPorId(tramiteId).orElse(null) : null;

                DetalleTrabajo detalle = DetalleTrabajo.builder()
                        .tramite(tramite)
                        .precioServicio((detallePrecio != null && detallePrecio.size() > i) ? detallePrecio.get(i) : null)
                        .observaciones((detalleObservaciones != null && detalleObservaciones.size() > i) ? detalleObservaciones.get(i) : null)
                        .ruta((detalleRuta != null && detalleRuta.size() > i) ? detalleRuta.get(i) : null)
                        .build();

                detalles.add(detalle);
            }
        }
        pedido.setDetalles(detalles);

        model.addAttribute("pedido", pedido);
        agregarDatosDeApoyo(model);
        model.addAttribute("tituloFormulario", pedido.getId() == null ? "Nuevo pedido" : "Editar pedido");
        model.addAttribute("modulo", "pedidos");

        return "pedidos/formulario";
    }

    // detalle completo de un pedido, para el modal de "Detalles" del listado
    @GetMapping("/pedidos/{id}/detalle")
    @ResponseBody
    @Transactional(readOnly = true) // permite leer relaciones lazy (cliente, empleado, detalles) sin cerrar la sesion antes de tiempo
    public ResponseEntity<Map<String, Object>> obtenerDetalle(@PathVariable Long id) {

        return pedidoService.buscarPorId(id)
                .map(pedido -> ResponseEntity.ok(mapearDetallePedido(pedido)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
     * Arma la respuesta JSON del modal de detalle de un pedido. No se
     * devuelve la entidad Pedido directamente porque aqui necesitamos
     * combinar datos de varias tablas (el nombre_usuario del empleado no
     * esta en Persona/Pedido, hay que ir a buscarlo aparte a Usuario) y
     * evitar mandar relaciones completas que el navegador no necesita.
     */
    private Map<String, Object> mapearDetallePedido(Pedido pedido) {

        Map<String, Object> resultado = new LinkedHashMap<>();

        Map<String, Object> datosPedido = new LinkedHashMap<>();
        datosPedido.put("id", pedido.getId());
        datosPedido.put("fechaRegistro", pedido.getFechaRegistro());
        datosPedido.put("montoTotal", pedido.getMontoTotal());
        resultado.put("pedido", datosPedido);

        resultado.put("cliente", mapearPersona(pedido.getCliente(), false));

        // al empleado si le agregamos su nombre_usuario (dato solo util
        // para quien administra el sistema, de ahi el pedido original)
        resultado.put("empleado", mapearPersona(pedido.getEmpleado(), true));

        List<Map<String, Object>> trabajos = pedido.getDetalles().stream()
                .map(detalle -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", detalle.getId());
                    item.put("tramite", detalle.getTramite().getNombre());
                    item.put("institucion", detalle.getTramite().getInstitucion().getNombre());
                    item.put("precio", detalle.getPrecioServicio());
                    item.put("observaciones", detalle.getObservaciones());
                    item.put("tieneImagen", detalle.getRuta() != null && !detalle.getRuta().isBlank());
                    return item;
                })
                .toList();
        resultado.put("trabajos", trabajos);

        return resultado;
    }

    private Map<String, Object> mapearPersona(Persona persona, boolean incluirUsuario) {

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("nombres", persona.getNombres());
        item.put("apellidos", persona.getApellidos());
        item.put("telefono", persona.getTelefono());
        item.put("correo", persona.getCorreo());
        item.put("documentoIdentidad", persona.getDocumentoIdentidad());

        if (incluirUsuario) {
            // no toda Persona tiene una cuenta de Usuario asociada (un
            // cliente normal no la tiene), por eso es opcional
            Optional<Usuario> usuario = usuarioRepository.findByPersonaId(persona.getId());
            item.put("nombreUsuario", usuario.map(Usuario::getNombreUsuario).orElse(null));
            item.put("rol", usuario.map(Usuario::getRol).orElse(null));
        }

        return item;
    }

    // exporta el listado completo de pedidos a PDF (boton "Imprimir")
    @GetMapping("/pedidos/pdf")
    public ResponseEntity<byte[]> exportarPdf(@RequestParam(required = false) String buscar,
                                              @RequestParam(required = false) String fecha) {

        // mismo criterio de "fecha" que en listar(): sin parametro = hoy
        LocalDate fechaFiltro = (fecha == null) ? LocalDate.now() : (fecha.isBlank() ? null : LocalDate.parse(fecha));

        List<String> encabezados = List.of("N°", "Cliente", "Atendido por", "Fecha", "Monto total");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<List<String>> filas = pedidoService.filtrar(fechaFiltro, buscar).stream()
                .map(pedido -> List.of(
                        String.valueOf(pedido.getId()),
                        pedido.getCliente().getNombres() + " " + pedido.getCliente().getApellidos(),
                        pedido.getEmpleado().getNombres() + " " + pedido.getEmpleado().getApellidos(),
                        pedido.getFechaRegistro().format(formatoFecha),
                        "Bs. " + pedido.getMontoTotal()
                ))
                .toList();

        String titulo = fechaFiltro != null
                ? "Pedidos del " + fechaFiltro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "Pedidos";

        byte[] pdf = pdfService.generarListado(titulo, encabezados, filas);
        return pdfService.responder(pdf, "pedidos.pdf");
    }

    // exporta el detalle de UN pedido a PDF, con la misma informacion que
    // ya arma mapearDetallePedido() para el modal de "Detalles" del listado
    @GetMapping("/pedidos/{id}/pdf")
    @Transactional(readOnly = true) // permite leer relaciones lazy (cliente, empleado, detalles)
    public ResponseEntity<byte[]> exportarDetallePdf(@PathVariable Long id) {

        Pedido pedido = pedidoService.buscarPorId(id).orElseThrow();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Persona cliente = pedido.getCliente();
        Persona empleado = pedido.getEmpleado();
        String nombreUsuarioEmpleado = usuarioRepository.findByPersonaId(empleado.getId())
                .map(Usuario::getNombreUsuario)
                .orElse("-");

        List<PdfService.SeccionDetalle> secciones = new ArrayList<>();

        secciones.add(new PdfService.SeccionDetalle("Cliente", List.of(
                new String[]{"Nombre", cliente.getNombres() + " " + cliente.getApellidos()},
                new String[]{"Documento", cliente.getDocumentoIdentidad()},
                new String[]{"Teléfono", cliente.getTelefono()},
                new String[]{"Correo", cliente.getCorreo()}
        )));

        secciones.add(new PdfService.SeccionDetalle("Atendido por", List.of(
                new String[]{"Nombre", empleado.getNombres() + " " + empleado.getApellidos()},
                new String[]{"Usuario", nombreUsuarioEmpleado}
        )));

        // una fila por cada trabajo del pedido
        List<String[]> filasTrabajos = pedido.getDetalles().stream()
                .map(detalle -> new String[]{
                        detalle.getTramite().getNombre() + " (" + detalle.getTramite().getInstitucion().getNombre() + ")",
                        "Bs. " + detalle.getPrecioServicio() + (detalle.getObservaciones() != null ? " - " + detalle.getObservaciones() : "")
                })
                .toList();
        secciones.add(new PdfService.SeccionDetalle("Trabajos", filasTrabajos));

        secciones.add(new PdfService.SeccionDetalle("Resumen", List.of(
                new String[]{"Fecha de registro", pedido.getFechaRegistro().format(formatoFecha)},
                new String[]{"Monto total", "Bs. " + pedido.getMontoTotal()}
        )));

        byte[] pdf = pdfService.generarDetalle("Pedido #" + pedido.getId(), secciones);
        return pdfService.responder(pdf, "pedido-" + pedido.getId() + ".pdf");
    }

    // eliminar
    @GetMapping("/pedidos/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        pedidoService.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Pedido eliminado correctamente"
        );

        return "redirect:/pedidos";
    }

    /*
     * Abre el explorador de archivos NATIVO de Windows (un JFileChooser
     * de Swing) para elegir la imagen del trabajo terminado, y devuelve
     * la ruta absoluta elegida como texto para autocompletar el campo
     * del formulario.
     *
     * IMPORTANTE: esta ventana se abre en el escritorio del equipo
     * donde corre el SERVIDOR, no en la computadora de quien hizo clic
     * (los navegadores, por seguridad, nunca entregan la ruta real de
     * un archivo). Tiene sentido en este proyecto porque el estudio usa
     * la app desde la misma PC que la ejecuta; no funcionaria como se
     * espera si el servidor se accede en red desde otra computadora, y
     * tampoco funciona corriendo dentro de Docker (no tiene escritorio).
     */
    @GetMapping("/pedidos/seleccionar-archivo")
    @ResponseBody
    public ResponseEntity<Map<String, String>> seleccionarArchivo() {

        AtomicReference<String> rutaElegida = new AtomicReference<>(null);

        try {
            SwingUtilities.invokeAndWait(() -> {

                JFrame ventanaTemporal = new JFrame();
                ventanaTemporal.setAlwaysOnTop(true);
                ventanaTemporal.toFront();

                JFileChooser selector = new JFileChooser();
                selector.setDialogTitle("Selecciona la imagen del trabajo realizado");
                selector.setFileSelectionMode(JFileChooser.FILES_ONLY);
                selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Imágenes", "jpg", "jpeg", "png", "webp", "gif", "bmp"));

                int resultado = selector.showOpenDialog(ventanaTemporal);

                if (resultado == JFileChooser.APPROVE_OPTION) {
                    rutaElegida.set(selector.getSelectedFile().getAbsolutePath());
                }

                ventanaTemporal.dispose();
            });
        } catch (Exception e) {
            log.warn("No se pudo abrir el explorador de archivos nativo: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "No se pudo abrir el explorador de archivos en este equipo. " +
                            "Puedes escribir o pegar la ruta manualmente en el campo."));
        }

        // rutaElegida queda en null si el usuario cerro el dialogo sin
        // elegir nada (boton "Cancelar"); el frontend simplemente no
        // completa el campo en ese caso
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("ruta", rutaElegida.get());
        return ResponseEntity.ok(respuesta);
    }

    /*
     * Sirve la imagen del trabajo realizado leyendola directamente desde
     * la ruta guardada en detalle_trabajo.ruta. No se usa un recurso
     * estatico de carpeta fija (como con las fotos de cliente) porque
     * aqui la ruta puede apuntar a CUALQUIER lugar del disco elegido por
     * el usuario, no a una carpeta predefinida.
     */
    @GetMapping("/detalle-trabajo/{id}/imagen")
    public ResponseEntity<FileSystemResource> verImagenTrabajo(@PathVariable Long id) {

        DetalleTrabajo detalle = detalleTrabajoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));

        String ruta = detalle.getRuta();
        if (ruta == null || ruta.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path archivo = Path.of(ruta);
        if (!Files.exists(archivo) || !Files.isRegularFile(archivo)) {
            return ResponseEntity.notFound().build();
        }

        MediaType tipoContenido;
        try {
            String tipoDetectado = Files.probeContentType(archivo);
            tipoContenido = tipoDetectado != null ? MediaType.parseMediaType(tipoDetectado) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (IOException e) {
            tipoContenido = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(tipoContenido)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(new FileSystemResource(archivo));
    }
}