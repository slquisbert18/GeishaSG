/*
=========================================================
                GEISHA - APP.JS
=========================================================

Este archivo contiene el comportamiento general de la
interfaz del sistema.

Actualmente implementa:

✓ Sidebar colapsable
✓ Modal reutilizable
✓ Confirmaciones
✓ Mensajes de éxito
✓ Mensajes de error

Más adelante también contendrá:

- Notificaciones
- Toasts
- Paginación dinámica
- Filtros
- Etc.

=========================================================
*/


/*=======================================================
                SIDEBAR
=======================================================*/
const sidebar = document.querySelector(".sidebar");
const btnMenu = document.getElementById("btnMenu");
// Contrae o expande el menú lateral
if(btnMenu){

    btnMenu.addEventListener("click",function(){

        sidebar.classList.toggle("collapsed");

    });

}


// REFERENCIAS DEL MODAL
const modal = document.getElementById("modal");
const modalIcon = document.getElementById("modalIcon");
const modalTitle = document.getElementById("modalTitle");
const modalMessage = document.getElementById("modalMessage");
const btnAceptar = document.getElementById("btnAceptar");
const btnCancelar = document.getElementById("btnCancelar");

// REFERENCIAS DEL MODAL DE ESPECIFICACION FOTOGRAFICA
const modalFoto = document.getElementById("modalFoto");
const fotoTitulo = document.getElementById("fotoTitulo");
const fotoSubtitulo = document.getElementById("fotoSubtitulo");
const fotoContenido = document.getElementById("fotoContenido");
const fotoVacio = document.getElementById("fotoVacio");
const btnCerrarFoto = document.getElementById("btnCerrarFoto");
const btnCerrarFotoSup = document.getElementById("btnCerrarFotoSup");
const btnEditarFoto = document.getElementById("btnEditarFoto");
const btnAgregarFoto = document.getElementById("btnAgregarFoto");

let tramiteSeleccionado = null;

// ABRIR MODAL
function mostrarModal(config){
    // Título
    modalTitle.textContent = config.titulo;

    // Mensaje
    modalMessage.textContent = config.mensaje;

    // Texto botones
    btnAceptar.textContent =
        config.textoAceptar || "Aceptar";

    btnCancelar.textContent =
        config.textoCancelar || "Cancelar";

    // Mostrar u ocultar botón cancelar
    btnCancelar.style.display =
        config.mostrarCancelar
            ? "inline-flex"
            : "none";

    // Color botón principal
    btnAceptar.style.background = config.colorBoton || "#2F241D";

    // Cambiar icono según tipo
    switch(config.tipo){
        case "success":
            modalIcon.innerHTML = '<i class="bi bi-check-circle-fill"></i>';
            modalIcon.style.color="#2E7D32";
            break;

        case "error":
            modalIcon.innerHTML = '<i class="bi bi-x-circle-fill"></i>';
            modalIcon.style.color="#C62828";
            break;

        case "warning":
            modalIcon.innerHTML = '<i class="bi bi-exclamation-triangle-fill"></i>';
            modalIcon.style.color="#F9A825";
            break;

        case "info":
            modalIcon.innerHTML = '<i class="bi bi-info-circle-fill"></i>';
            modalIcon.style.color="#1976D2";
            break;
    }

    // Acción botón aceptar
    btnAceptar.onclick=function(){
        cerrarModal();
        if(config.onAceptar){
            config.onAceptar();
        }
    };

    // Acción botón cancelar
    btnCancelar.onclick=function(){
        cerrarModal();
    };

    // Mostrar modal
    modal.classList.add("show");

}

// abrir modal especificaciones
function abrirModalFoto(){
    modalFoto.classList.add("show");
}

// CERRAR MODAL
function cerrarModal(){
    modal.classList.remove("show");
}

// cerrar modal especificaciones
function cerrarModalFoto(){
    modalFoto.classList.remove("show");
}

btnCerrarFoto.onclick=cerrarModalFoto;
btnCerrarFotoSup.onclick=cerrarModalFoto;

btnAgregarFoto.addEventListener("click", function(){
    window.location.href = "/especificaciones/nuevo/" + tramiteSeleccionado;
});

btnEditarFoto.addEventListener("click", function(){
    window.location.href = "/especificaciones/editar/" + tramiteSeleccionado;
})

// mostrar (si existe) especificacion fotografica de un tramite
function mostrarEspecificacion(datos){
    fotoTitulo.textContent = "Especificación fotográfica";
    fotoSubtitulo.textContent = "Trámite: " + datos.tramite;
    fotoContenido.style.display = "block";
    fotoVacio.style.display = "none";
    btnEditarFoto.style.display = "inline-flex";
    btnAgregarFoto.style.display = "none";

    document.getElementById("detalleAncho").textContent = datos.ancho;
    document.getElementById("detalleAlto").textContent = datos.alto;
    document.getElementById("detalleCantidad").textContent = datos.cantidad;
    document.getElementById("detalleColor").textContent = datos.color;
    document.getElementById("detallePapel").textContent = datos.papel;
    document.getElementById("detalleRgb").textContent = datos.rgb;
    document.getElementById("detalleHex").textContent = datos.hex;
    document.getElementById("detalleResolucion").textContent = datos.resolucion;
    document.getElementById("detalleMargen").textContent = datos.margen;
    document.getElementById("detalleObservaciones").textContent = datos.observaciones;

    abrirModalFoto();
}

// en caso de no tener especificacion fotografica de un tramite
function mostrarSinEspecificacion(nombreTramite){
    fotoTitulo.textContent = "Especificación fotográfica";
    fotoSubtitulo.textContent = "Trámite: " + nombreTramite;
    fotoContenido.style.display = "none";
    fotoVacio.style.display = "block";
    btnEditarFoto.style.display = "none";
    btnAgregarFoto.style.display = "inline-flex";
    abrirModalFoto();
}

//ATAJOS

// Mensaje de éxito
function mostrarExito(mensaje){
    mostrarModal({
        tipo:"success",
        titulo:"Operación realizada",
        mensaje:mensaje,
        textoAceptar:"Aceptar",
        mostrarCancelar:false,
        colorBoton:"#2E7D32"
    });
}

// Mensaje de error
function mostrarError(mensaje){
    mostrarModal({
        tipo:"error",
        titulo:"Ha ocurrido un error",
        mensaje:mensaje,
        textoAceptar:"Aceptar",
        mostrarCancelar:false,
        colorBoton:"#C62828"
    });
}

// Mensaje informativo
function mostrarInformacion(titulo,mensaje){
    mostrarModal({
        tipo:"info",
        titulo:titulo,
        mensaje:mensaje,
        textoAceptar:"Aceptar",
        mostrarCancelar:false,
        colorBoton:"#1976D2"
    });
}

// Confirmación
function confirmar(titulo, mensaje, onAceptar){
    mostrarModal({
        tipo:"warning",
        titulo:titulo,
        mensaje:mensaje,
        textoAceptar:"Eliminar",
        textoCancelar:"Cancelar",
        mostrarCancelar:true,
        colorBoton:"#C62828",
        onAceptar:onAceptar
    });
}

/*
=======================================================
MENSAJES ENVIADOS POR SPRING
=======================================================
*/
document.addEventListener("DOMContentLoaded", function () {
    const mensaje = document.getElementById("mensajeExito");
    if (mensaje) {
        mostrarExito(
            mensaje.dataset.mensaje
        );
    }
});

/*
=======================================================
CONFIRMACIÓN DE ELIMINACIÓN
=======================================================
*/

document.addEventListener("DOMContentLoaded", function () {
    // Buscamos todos los botones eliminar
    const botonesEliminar =
        document.querySelectorAll(".btn-eliminar");

    botonesEliminar.forEach(function(boton){
        boton.addEventListener("click", function(e){

            e.preventDefault();

            const id = this.dataset.id;
            const nombre = this.dataset.nombre;
            const url = this.dataset.url;
            const entidad = this.dataset.entidad;
            confirmar(
                "Eliminar " + entidad,
                "¿Está seguro de eliminar el " + entidad +
                " \"" + nombre + "\"?\n\nEsta acción no podrá deshacerse.",

                function(){
                    window.location.href = url + "/" + id;
                }
            );
        });
    });
});

// ESPECIFICACION FOTOGRAFICA
document.addEventListener("DOMContentLoaded", function(){
    const botonesFoto = document.querySelectorAll(".btn-foto");
    botonesFoto.forEach(function (boton){
        boton.addEventListener("click", function(){
            const tramiteId = this.dataset.id;
            cargarEspecificacion(tramiteId);
        });
    });
});

async function cargarEspecificacion(tramiteId){
    tramiteSeleccionado = tramiteId;
    const boton = document.querySelector('.btn-foto[data-id="' + tramiteId + '"]');
    const nombreTramite = boton.dataset.tramite;
    try{
        const respuesta = await fetch("/tramites/" + tramiteId + "/especificacion");

        // el tramite no tiene especificacion
        if(respuesta.status === 404){
            // const tramite = document.querySelector('[data-id="' + tramiteId + '"]');
            mostrarSinEspecificacion(nombreTramite);
            return;
        }

        // otro error del servidor
        if(!respuesta.ok){
            throw new Error("Error al consultar la especificacion");
        }

        const especificacion = await respuesta.json();

        // abrimos el modal
        if(especificacion){
            mostrarEspecificacion({
                //tramite: document.querySelector('[data-id="'+tramiteId+'"]').dataset.tramite,
                tramite: nombreTramite,
                ancho: especificacion.ancho + " cm",
                alto: especificacion.alto + " cm",
                cantidad: especificacion.cantidad,
                color: especificacion.colorFondo,
                papel: especificacion.tipoPapel,
                rgb: especificacion.codigoRgb ?? "-",
                hex: especificacion.codigoHex ?? "-",
                resolucion: especificacion.resolucion ?
                    especificacion.resolucion + " dpi" :
                    "-",
                margen: especificacion.margen ?
                    especificacion.margen + " mm" :
                    "-",
                observaciones: especificacion.observaciones ?? "-"
            });
        }
        else{
            //mostrarSinEspecificacion(document.querySelector('[data-id="'+tramiteId+'"]').dataset.tramite);
            mostrarSinEspecificacion(nombreTramite);
        }

    }
    catch(error){
        console.error(error);
        mostrarError("No fue posible obtener la especificacion fotografica");
    }
}

/*
 * Combobox de busqueda: junta un <input type="text"> con una lista
 * desplegable filtrable y un <input type="hidden"> que guarda el id de
 * lo que el usuario selecciono. Se usa para reemplazar los <select>
 * tradicionales cuando la lista de opciones es larga y conviene poder
 * escribir para filtrar (clientes, tramites, etc).
 */
function crearCombobox({ input, hidden, dropdown, items, getLabel, getId, onSelect }) {

    // dibuja la lista de coincidencias segun lo que el usuario escribio
    function render(filtro) {
        dropdown.innerHTML = "";

        const texto = filtro.trim().toLowerCase();

        // sin texto se muestran todas las opciones; con texto, solo las
        // que contienen ese texto en su nombre (busqueda simple, no
        // distingue mayusculas/minusculas ni tildes)
        const coincidencias = items.filter(item =>
            getLabel(item).toLowerCase().includes(texto)
        );

        if (coincidencias.length === 0) {
            dropdown.classList.remove("show");
            return;
        }

        coincidencias.forEach(item => {
            const li = document.createElement("li");
            li.textContent = getLabel(item);

            // "mousedown" en vez de "click": el mousedown ocurre ANTES
            // que el "blur" del input, asi la seleccion se registra antes
            // de que el listener de blur cierre la lista
            li.addEventListener("mousedown", (evento) => {
                evento.preventDefault();

                input.value = getLabel(item);
                hidden.value = getId(item);

                dropdown.classList.remove("show");

                if (onSelect) {
                    onSelect(item);
                }
            });

            dropdown.appendChild(li);
        });

        dropdown.classList.add("show");
    }

    // al escribir, la seleccion anterior deja de ser valida hasta que
    // el usuario elija una opcion de la lista de nuevo
    input.addEventListener("input", () => {
        hidden.value = "";
        render(input.value);
    });

    // al enfocar el input tambien se muestra la lista completa (si esta
    // vacio) o filtrada (si ya tenia texto), para no obligar a borrar y
    // volver a escribir
    input.addEventListener("focus", () => render(input.value));

    // clic fuera del combobox: cierra la lista
    document.addEventListener("click", (evento) => {
        if (!input.contains(evento.target) && !dropdown.contains(evento.target)) {
            dropdown.classList.remove("show");
        }
    });
}


/*
 * Logica del formulario de pedido:
 *  - combobox de cliente (busqueda con texto)
 *  - filas de "trabajos" (detalle_trabajo) que se pueden agregar/quitar,
 *    cada una con su propio combobox de tramite
 *  - suma en vivo de los precios de cada fila para mostrar el monto total
 *
 * CLIENTES y TRAMITES son arreglos globales que llegan ya listos desde el
 * servidor, asi este script no necesita pedirlos por su cuenta.
 */
document.addEventListener("DOMContentLoaded", () => {

    const contenedorFilas = document.querySelector("#filasTrabajos");
    const plantillaFila = document.querySelector("#plantillaFilaTrabajo");
    const btnAgregarFila = document.querySelector("#btnAgregarTrabajo");
    const montoTotalDisplay = document.querySelector("#montoTotalDisplay");

    // ===== combobox de cliente =====
    // (input, oculto y lista ya vienen renderizados en el HTML; si el
    // pedido es una edicion, tambien vienen con el cliente actual precargado)
    crearCombobox({
        input: document.querySelector("#clienteInput"),
        hidden: document.querySelector("#clienteHidden"),
        dropdown: document.querySelector("#clienteDropdown"),
        items: CLIENTES,
        getLabel: (cliente) => cliente.nombre,
        getId: (cliente) => cliente.id
    });

    // recalcula el monto total sumando el precio de cada fila visible
    function recalcularTotal() {
        const total = [...contenedorFilas.querySelectorAll(".precio-input")]
            .reduce((suma, input) => suma + (parseFloat(input.value) || 0), 0);

        montoTotalDisplay.value = total.toFixed(2);
    }

    // conecta el combobox de tramite y los listeners de una fila de trabajo
    // (se llama tanto para las filas que ya vienen en el HTML -edicion- como
    // para las que se agregan despues con el boton "+")
    function inicializarFila(fila) {

        const tramiteInput = fila.querySelector(".tramite-input");
        const tramiteHidden = fila.querySelector(".tramite-id-input");
        const tramiteDropdown = fila.querySelector(".tramite-dropdown");
        const precioInput = fila.querySelector(".precio-input");

        crearCombobox({
            input: tramiteInput,
            hidden: tramiteHidden,
            dropdown: tramiteDropdown,
            items: TRAMITES,
            getLabel: (tramite) => tramite.nombre,
            getId: (tramite) => tramite.id,
            // al elegir un tramite se sugiere su precio_base como precio
            // del servicio; el usuario igual puede editarlo despues
            onSelect: (tramite) => {
                precioInput.value = tramite.precio;
                recalcularTotal();
            }
        });

        // cualquier cambio en el precio de esta fila actualiza el total
        precioInput.addEventListener("input", recalcularTotal);

        // boton de eliminar fila: la quita del DOM y recalcula el total
        fila.querySelector(".btn-eliminar-fila").addEventListener("click", () => {
            fila.remove();
            recalcularTotal();
        });
    }

    // agrega una fila nueva y vacia clonando la plantilla <template>
    function agregarFila() {
        const fila = plantillaFila.content.firstElementChild.cloneNode(true);
        contenedorFilas.appendChild(fila);
        inicializarFila(fila);
    }

    btnAgregarFila.addEventListener("click", agregarFila);

    // filas que ya existian al cargar la pagina (pedido en edicion): solo
    // hay que conectarles el combobox y los listeners, no clonarlas
    contenedorFilas.querySelectorAll(".detalle-row").forEach(inicializarFila);

    // pedido nuevo sin ningun trabajo todavia: se arranca con una fila
    // vacia para que el usuario no tenga que hacer clic en "+" primero
    if (contenedorFilas.children.length === 0) {
        agregarFila();
    }

    recalcularTotal();
});


/*=======================================================
        MODAL DE DETALLE DE PEDIDO
=======================================================*/

// referencias del modal (pueden no existir en paginas sin layout completo,
// por eso se valida con "if(modalPedido)" mas abajo antes de usarlas)
const modalPedido = document.getElementById("modalPedido");
const pedidoSubtitulo = document.getElementById("pedidoSubtitulo");
const pedidoClienteNombre = document.getElementById("pedidoClienteNombre");
const pedidoClienteDocumento = document.getElementById("pedidoClienteDocumento");
const pedidoClienteTelefono = document.getElementById("pedidoClienteTelefono");
const pedidoClienteCorreo = document.getElementById("pedidoClienteCorreo");
const pedidoEmpleadoNombre = document.getElementById("pedidoEmpleadoNombre");
const pedidoEmpleadoUsuario = document.getElementById("pedidoEmpleadoUsuario");
const pedidoEmpleadoRol = document.getElementById("pedidoEmpleadoRol");
const pedidoTrabajosLista = document.getElementById("pedidoTrabajosLista");
const pedidoFecha = document.getElementById("pedidoFecha");
const pedidoMontoTotal = document.getElementById("pedidoMontoTotal");
const btnCerrarPedido = document.getElementById("btnCerrarPedido");
const btnCerrarPedidoSup = document.getElementById("btnCerrarPedidoSup");

function abrirModalPedido() {
    modalPedido.classList.add("show");
}

function cerrarModalPedido() {
    modalPedido.classList.remove("show");
}

if (modalPedido) {
    btnCerrarPedido.onclick = cerrarModalPedido;
    btnCerrarPedidoSup.onclick = cerrarModalPedido;
}

// muestra un valor o un guion si viene null/undefined (campos opcionales
// como telefono, correo, observaciones)
function valorOGuion(valor) {
    return (valor === null || valor === undefined || valor === "") ? "-" : valor;
}

// pinta en el modal la respuesta de GET /pedidos/{id}/detalle
function mostrarDetallePedido(datos) {

    pedidoSubtitulo.textContent = "Pedido #" + datos.pedido.id;

    // cliente
    pedidoClienteNombre.textContent = datos.cliente.nombres + " " + datos.cliente.apellidos;
    pedidoClienteDocumento.textContent = valorOGuion(datos.cliente.documentoIdentidad);
    pedidoClienteTelefono.textContent = valorOGuion(datos.cliente.telefono);
    pedidoClienteCorreo.textContent = valorOGuion(datos.cliente.correo);

    // quien atendio
    pedidoEmpleadoNombre.textContent = datos.empleado.nombres + " " + datos.empleado.apellidos;
    pedidoEmpleadoUsuario.textContent = valorOGuion(datos.empleado.nombreUsuario);
    pedidoEmpleadoRol.textContent = valorOGuion(datos.empleado.rol);

    // trabajos: se reconstruye la lista completa cada vez que se abre el modal
    pedidoTrabajosLista.innerHTML = "";
    datos.trabajos.forEach(trabajo => {
        const fila = document.createElement("div");
        fila.className = "pedido-trabajo-item";
        fila.innerHTML =
            '<div class="pedido-trabajo-info">' +
            '<strong>' + trabajo.tramite + '</strong>' +
            '<span>' + trabajo.institucion + '</span>' +
            (trabajo.observaciones ? '<span class="pedido-trabajo-obs">' + trabajo.observaciones + '</span>' : '') +
            '</div>' +
            '<div class="pedido-trabajo-precio">Bs. ' + Number(trabajo.precio).toFixed(2) + '</div>';
        pedidoTrabajosLista.appendChild(fila);
    });

    // fecha: llega como texto ISO ("2026-08-22T10:15:00"), se muestra en
    // formato local legible
    const fecha = new Date(datos.pedido.fechaRegistro);
    pedidoFecha.textContent = fecha.toLocaleString("es-BO", {
        day: "2-digit", month: "2-digit", year: "numeric",
        hour: "2-digit", minute: "2-digit"
    });

    pedidoMontoTotal.textContent = "Bs. " + Number(datos.pedido.montoTotal).toFixed(2);

    abrirModalPedido();
}

async function cargarDetallePedido(pedidoId) {
    try {
        const respuesta = await fetch("/pedidos/" + pedidoId + "/detalle");

        if (!respuesta.ok) {
            throw new Error("Error al consultar el detalle del pedido");
        }

        const datos = await respuesta.json();
        mostrarDetallePedido(datos);

    } catch (error) {
        console.error(error);
        mostrarError("No fue posible obtener el detalle del pedido");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".btn-detalle-pedido").forEach(function (boton) {
        boton.addEventListener("click", function () {
            cargarDetallePedido(this.dataset.id);
        });
    });
});


/*=======================================================
        MODAL "NUEVO CLIENTE RAPIDO" (desde el formulario de pedido)
=======================================================*/
const modalCliente = document.getElementById("modalCliente");

if (modalCliente) {

    const campoNombres = document.getElementById("clienteRapidoNombres");
    const campoApellidos = document.getElementById("clienteRapidoApellidos");
    const campoTelefono = document.getElementById("clienteRapidoTelefono");
    const campoCorreo = document.getElementById("clienteRapidoCorreo");
    const campoDocumento = document.getElementById("clienteRapidoDocumento");

    // un <p class="field-error"> por cada campo, para mostrar los
    // mensajes de validacion que devuelve el servidor
    const camposConError = {
        nombres: [campoNombres, document.getElementById("errorClienteRapidoNombres")],
        apellidos: [campoApellidos, document.getElementById("errorClienteRapidoApellidos")],
        telefono: [campoTelefono, document.getElementById("errorClienteRapidoTelefono")],
        correo: [campoCorreo, document.getElementById("errorClienteRapidoCorreo")],
        documentoIdentidad: [campoDocumento, document.getElementById("errorClienteRapidoDocumento")]
    };

    function limpiarFormularioClienteRapido() {
        campoNombres.value = "";
        campoApellidos.value = "";
        campoTelefono.value = "";
        campoCorreo.value = "";
        campoDocumento.value = "";
        // limpia todos los mensajes de error previos
        Object.values(camposConError).forEach(([, elementoError]) => {
            elementoError.textContent = "";
        });
    }

    function mostrarErroresClienteRapido(errores) {
        // primero se limpian los mensajes anteriores, luego se pintan
        // solo los que vinieron en la respuesta del servidor
        Object.values(camposConError).forEach(([, elementoError]) => {
            elementoError.textContent = "";
        });
        Object.entries(errores).forEach(([campo, mensaje]) => {
            if (camposConError[campo]) {
                camposConError[campo][1].textContent = mensaje;
            }
        });
    }

    document.getElementById("btnNuevoCliente")?.addEventListener("click", () => {
        limpiarFormularioClienteRapido();
        modalCliente.classList.add("show");
    });

    document.getElementById("btnCancelarClienteRapido").addEventListener("click", () => {
        modalCliente.classList.remove("show");
    });

    document.getElementById("btnCerrarClienteRapido").addEventListener("click", () => {
        modalCliente.classList.remove("show");
    });

    document.getElementById("btnGuardarClienteRapido").addEventListener("click", async () => {

        const persona = {
            nombres: campoNombres.value.trim(),
            apellidos: campoApellidos.value.trim(),
            telefono: campoTelefono.value.trim() || null,
            correo: campoCorreo.value.trim() || null,
            documentoIdentidad: campoDocumento.value.trim() || null
        };

        try {
            const respuesta = await fetch("/personas/rapido", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(persona)
            });

            if (!respuesta.ok) {
                // 400: el cuerpo trae {campo: mensaje} por cada error de validacion
                const errores = await respuesta.json();
                mostrarErroresClienteRapido(errores);
                return;
            }

            const clienteCreado = await respuesta.json();

            // se agrega a la lista en memoria que usa el combobox, para
            // que aparezca en futuras busquedas sin recargar la pagina
            CLIENTES.push(clienteCreado);

            // y queda seleccionado de inmediato en el pedido
            document.getElementById("clienteInput").value = clienteCreado.nombre;
            document.getElementById("clienteHidden").value = clienteCreado.id;

            modalCliente.classList.remove("show");

        } catch (error) {
            console.error(error);
            mostrarError("No fue posible registrar el cliente");
        }
    });
}