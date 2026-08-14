package com.geisha.controller;

import com.geisha.entity.Institucion;
import com.geisha.exception.NombreDuplicadoException;
import com.geisha.service.InstitucionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * Un Controller recibe las peticiones del navegador.
 *
 * Su responsabilidad es:
 * 1. Recibir la petición.
 * 2. Solicitar información al Service.
 * 3. Enviar esa información a la vista (HTML).
 */
@Controller
public class InstitucionController {

    private final InstitucionService institucionService;

    /*
     * Spring inyecta automáticamente el servicio.
     */
    public InstitucionController(InstitucionService institucionService) {
        this.institucionService = institucionService;
    }

    /*
     * Cuando el usuario visite:
     *
     * http://localhost:8080/instituciones
     *
     * se ejecutará este método.
     */
    @GetMapping("/instituciones")
    public String listarInstituciones(@RequestParam(required = false) String nombre, Model model) {
        /*
         * Si existe texto de búsqueda,
         * filtramos.
         *
         * Si está vacío,
         * mostramos todo.
         */
        if(nombre != null && !nombre.isBlank()){
            model.addAttribute("instituciones", institucionService.buscarPorNombre(nombre));
        }
        else{
            model.addAttribute("instituciones", institucionService.listarTodas());
        }

        // Título que se mostrará en la pestaña del navegador
        model.addAttribute("modulo", "instituciones");

        /*
         * Devuelve la vista:
         *
         * templates/instituciones/listar.html
         */
        return "instituciones/listar";
    }

    /*
     * Muestra el formulario para registrar una institución.
     */
    @GetMapping("/instituciones/nuevo")
    public String nuevo(Model model){

        // Objeto vacío para crear una nueva institución
        model.addAttribute("institucion", new Institucion());

        model.addAttribute("title","Nueva institución");
        model.addAttribute("tituloFormulario", "Nueva institución");
        model.addAttribute("modulo","instituciones");

        return "instituciones/formulario";

    }

    // muestra el formulario con los datos de una
    // institución para que el usuario pueda modificarlos
    @GetMapping("/instituciones/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        // Busca la institución por su id.
        Institucion institucion = institucionService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Institucion no encontrada"));

        // Envía la institución a la vista para rellenar el formulario.
        model.addAttribute("institucion", institucion);

        model.addAttribute("title", "Editar institución");
        model.addAttribute("tituloFormulario", "Editar institución");
        model.addAttribute("modulo", "instituciones");

        return "instituciones/formulario";
    }

    // Recibe los datos del formulario y guarda la institución
    @PostMapping("/instituciones/guardar")
    public String guardar(@Valid @ModelAttribute Institucion institucion,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // verificamos si existen errores de validación (@NotBlank, @Email, etc.).
        if (result.hasErrors()) {
            model.addAttribute(
                    "tituloFormulario",
                    institucion.getId() == null
                            ? "Nueva institución"
                            : "Editar institución"
            );

            model.addAttribute("modulo", "instituciones");

            return "instituciones/formulario";
        }

        try {
            institucionService.guardar(institucion);
            // Guardamos un mensaje temporal que será mostrado después del redirect.

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    institucion.getId() == null
                            ? "Institución registrada correctamente."
                            : "Institución actualizada correctamente."
            );
        }
        catch(NombreDuplicadoException ex) {
            // asociamos el error al campo correspondiente
            result.rejectValue(ex.getCampo(), "", ex.getMessage());

            model.addAttribute("tituloFormulario",
                    institucion.getId() == null
                            ? "Nueva institución"
                            : "Editar institución"
            );

            model.addAttribute("modulo", "instituciones");
            return "instituciones/formulario";
        }
        // si todo salio bien redirigimos al listado
        return "redirect:/instituciones";
    }

    // elimina una institución.
    @GetMapping("/instituciones/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes){
        institucionService.eliminar(id);
        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Institución eliminada correctamente."
        );

        return "redirect:/instituciones";
    }
}