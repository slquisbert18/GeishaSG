package com.geisha.controller;

import com.geisha.entity.EspecificacionFotografica;
import com.geisha.entity.Institucion;
import com.geisha.entity.Tramite;
import com.geisha.exception.NombreDuplicadoException;
import com.geisha.service.EspecificacionFotograficaService;
import com.geisha.service.InstitucionService;
import com.geisha.service.TramiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TramiteController {
    private final TramiteService tramiteService;
    private final InstitucionService institucionService;
    private final EspecificacionFotograficaService efService;

    // LISTAR
    @GetMapping("/tramites")
    public String listar(Model model, @RequestParam(required = false) String buscar) {

        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("tramites", tramiteService.buscarPorNombre(buscar));
        } else {
            model.addAttribute("tramites", tramiteService.listarTodos());
        }

        model.addAttribute("buscar", buscar);
        model.addAttribute("modulo", "tramites");

        return "tramites/listar";
    }

    // NUEVO
    @GetMapping("/tramites/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("tramite", new Tramite());

        model.addAttribute("instituciones", institucionService.listarTodas());

        model.addAttribute("tituloFormulario", "Nuevo trámite");

        model.addAttribute("modulo", "tramites");

        return "tramites/formulario";
    }

    // GUARDAR
    @PostMapping("/tramites/guardar")
    public String guardar(@Valid @ModelAttribute Tramite tramite,
                          BindingResult result,
                          @RequestParam(required = false) Long institucionId,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        // Validación manual de la institución
        if (institucionId == null) {
            result.rejectValue("institucion", "", "Debe seleccionar una institución.");
        }

        // Si hay errores de validación
        if (result.hasErrors()) {

            model.addAttribute("instituciones", institucionService.listarTodas());

            model.addAttribute("institucionId", institucionId);

            model.addAttribute("tituloFormulario", tramite.getId() == null
                    ? "Nuevo trámite"
                    : "Editar trámite");

            model.addAttribute("modulo", "tramites");

            return "tramites/formulario";
        }

        // Recuperamos la institución seleccionada
        Institucion institucion = institucionService.buscarPorId(institucionId).orElseThrow(() ->
                new RuntimeException("Institución no encontrada."));

        tramite.setInstitucion(institucion);

        boolean esEdicion = tramite.getId() != null;

        try {

            tramiteService.guardar(tramite);

        } catch (NombreDuplicadoException ex) {

            result.rejectValue(ex.getCampo(), "", ex.getMessage());

            model.addAttribute("instituciones", institucionService.listarTodas());

            model.addAttribute("institucionId", institucionId);

            model.addAttribute("tituloFormulario", tramite.getId() == null
                    ? "Nuevo trámite"
                    : "Editar trámite");

            model.addAttribute("modulo", "tramites");

            return "tramites/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                esEdicion
                        ? "Trámite actualizado correctamente."
                        : "Trámite registrado correctamente."
        );

        return "redirect:/tramites";
    }

    // EDITAR
    @GetMapping("/tramites/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Tramite tramite = tramiteService.buscarPorId(id).orElseThrow();

        model.addAttribute("tramite", tramite);

        model.addAttribute("instituciones", institucionService.listarTodas());

        model.addAttribute("tituloFormulario", "Editar trámite");

        model.addAttribute("modulo", "tramites");

        model.addAttribute("institucionId", tramite.getInstitucion().getId());

        return "tramites/formulario";
    }

    // ELIMINAR
    @GetMapping("/tramites/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        tramiteService.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Trámite eliminado correctamente"
        );

        return "redirect:/tramites";
    }

    // si exista la especificacion fotografica de un tramite, se muestra
    // caso contrario se muestra una pantalla vacia
    @GetMapping("/tramites/{id}/especificacion")
    @ResponseBody
    public ResponseEntity<EspecificacionFotografica> obtenerEspecificacion(@PathVariable Long id){
        return efService.buscarPorTramite(id).map(ResponseEntity::ok).
                orElseGet(()->ResponseEntity.notFound().build());
    }
}
