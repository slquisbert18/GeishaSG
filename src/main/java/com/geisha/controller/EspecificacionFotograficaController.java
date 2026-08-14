package com.geisha.controller;

import com.geisha.entity.EspecificacionFotografica;
import com.geisha.entity.Tramite;
import com.geisha.service.EspecificacionFotograficaService;
import com.geisha.service.TramiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class EspecificacionFotograficaController {
    private final EspecificacionFotograficaService efService;
    private final TramiteService tramiteService;

    // en este caso no buscaremos las especificaciones fotografiacas ya que no tienen nombre
    // LISTAR
    @GetMapping("/especificaciones")
    public String listar(Model model){
        model.addAttribute("especificaciones", efService.listarTodos());
        model.addAttribute("modulo", "especificaciones");

        return "especificaciones/listar";
    }

    // NUEVO
    @GetMapping("/especificaciones/nuevo/{tramiteId}")
    public String nuevo(@PathVariable Long tramiteId, Model model){
        Tramite tramite = tramiteService.buscarPorId(tramiteId).orElseThrow();
        EspecificacionFotografica especificacion = new EspecificacionFotografica();
        especificacion.setTramite(tramite);
        model.addAttribute("especificacion", especificacion);
        //model.addAttribute("tramites", tramiteService.listarTodos());
        model.addAttribute("tituloFormulario", "Nueva especificacion fotografica");
        model.addAttribute("modulo", "tramites");

        return "especificaciones/formulario";
    }

    // GUARDAR
    @PostMapping("/especificaciones/guardar")
    public String guardar(@ModelAttribute EspecificacionFotografica especificacion, RedirectAttributes redirectAttributes){
        Long tramiteId = especificacion.getTramite().getId();

        // recuperamos el tramite seleccionado en el select
        Tramite tramite = tramiteService.buscarPorId(tramiteId).
                orElseThrow(()->new RuntimeException("Trámite no encontrado"));

        // asociamos el tramite a la especificacion
        especificacion.setTramite(tramite);

        boolean esEdicion = especificacion.getId() != null;

        // guardamos la especificacion
        efService.guardar(especificacion);

        // mensaje via flash attribute (sobrevive un solo redirect y evita
        // problemas de codificacion: tildes/espacios)
        redirectAttributes.addFlashAttribute("mensajeExito", esEdicion
                ? "Especificación fotográfica actualizada correctamente"
                : "Especificación fotográfica registrada correctamente");

        return "redirect:/tramites";
    }

    // EDITAR
    @GetMapping("/especificaciones/editar/{tramiteId}")
    public String editar(@PathVariable Long tramiteId, Model model){
        // buscamos la especificacion que se editara
        EspecificacionFotografica especificacion = efService.buscarPorTramite(tramiteId).
                orElseThrow(()-> new RuntimeException("Especificacion no encontrada"));

        model.addAttribute("especificacion", especificacion);

        // lista de tramites para el select
        //model.addAttribute("tramites", tramiteService.listarTodos());

        model.addAttribute("tituloFormulario", "Editar especificacion fotografica");
        model.addAttribute("modulo", "tramite");

        // tramite actualmente seleccionado
        //model.addAttribute("tramiteId", especificacion.getTramite().getId());

        return "especificaciones/formulario";
    }

    // ELIMINAR
    @GetMapping("/especificacion/eliminar/{id}")
    public String eliminar(@PathVariable Long id){
        efService.eliminar(id);

        return "redirect:/especificaciones";
    }
}
