package com.geisha.controller;

import com.geisha.dto.TrabajadorForm;
import com.geisha.service.TrabajadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    // LISTAR
    @GetMapping("/trabajadores")
    public String listar(Model model, @RequestParam(required = false) String buscar) {

        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("trabajadores", trabajadorService.buscar(buscar));
        } else {
            model.addAttribute("trabajadores", trabajadorService.listarTodos());
        }

        model.addAttribute("buscar", buscar);
        model.addAttribute("modulo", "trabajadores");

        return "trabajadores/listar";
    }

    // NUEVO
    @GetMapping("/trabajadores/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("trabajadorForm", new TrabajadorForm());
        model.addAttribute("tituloFormulario", "Nuevo usuario");
        model.addAttribute("modulo", "trabajadores");

        return "trabajadores/formulario";
    }

    // EDITAR
    @GetMapping("/trabajadores/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        model.addAttribute(
                "trabajadorForm",
                trabajadorService.buscarPorId(id)
        );

        model.addAttribute("tituloFormulario", "Editar trabajador");
        model.addAttribute("modulo", "trabajadores");

        return "trabajadores/formulario";
    }

    // GUARDAR
    @PostMapping("/trabajadores/guardar")
    public String guardar(
            @Valid @ModelAttribute("trabajadorForm") TrabajadorForm trabajadorForm,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "tituloFormulario",
                    trabajadorForm.getUsuario().getId() == null
                            ? "Nuevo trabajador"
                            : "Editar trabajador"
            );

            model.addAttribute("modulo", "trabajadores");

            return "trabajadores/formulario";
        }

        boolean esEdicion = trabajadorForm.getUsuario().getId() != null;

        try {

            trabajadorService.guardar(trabajadorForm);

        } catch (RuntimeException ex) {

            result.rejectValue(
                    "usuario.nombreUsuario",
                    "",
                    ex.getMessage()
            );

            model.addAttribute(
                    "tituloFormulario",
                    esEdicion
                            ? "Editar trabajador"
                            : "Nuevo trabajador"
            );

            model.addAttribute("modulo", "trabajadores");

            return "trabajadores/formulario";
        }

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                esEdicion
                        ? "Trabajador actualizado correctamente"
                        : "Trabajador registrado correctamente"
        );

        return "redirect:/trabajadores";
    }

    // ELIMINAR
    @GetMapping("/trabajadores/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        trabajadorService.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Trabajador eliminado correctamente"
        );

        return "redirect:/trabajadores";
    }
}