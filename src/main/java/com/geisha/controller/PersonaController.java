package com.geisha.controller;

import com.geisha.entity.Persona;
import com.geisha.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PersonaController {
    private final PersonaService personaService;

    // listar
    @GetMapping("/personas")
    public String listar(Model model, @RequestParam(required = false) String buscar){
        if(buscar != null && !buscar.isBlank()){
            model.addAttribute("personas", personaService.buscarPorNombreOApellido(buscar));
        }
        else{
            model.addAttribute("personas", personaService.listarClientes());
        }
        model.addAttribute("buscar", buscar);
        model.addAttribute("modulo", "personas");
        return "personas/listar";
    }

    // nuevo
    @GetMapping("/personas/nuevo")
    public String nuevo(Model model){
        model.addAttribute("persona", new Persona());
        model.addAttribute("tituloFormulario", "Nuevo cliente");
        model.addAttribute("modulo", "personas");
        return "personas/formulario";
    }

    // guardar
    @PostMapping("/personas/guardar")
    public String guardar(@Valid @ModelAttribute Persona persona,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model){
        // si hay errores de validacion
        if(result.hasErrors()){
            model.addAttribute("tituloFormulario", persona.getId() == null
                    ? "Nueva persona"
                    : "Editar persona");
            model.addAttribute("modulo", "personas");
            return "personas/formulario";
        }
        boolean esEdicion = persona.getId() != null;

        try{
            personaService.guardar(persona);
        }
        catch(RuntimeException ex){
            result.rejectValue("documentoIdentidad", "", ex.getMessage());
            model.addAttribute("tituloFormulario", persona.getId() == null
                    ? "Nueva persona"
                    : "Editar persona");
            model.addAttribute("modulo", "personas");
            return "personas/formulario";
        }

        redirectAttributes.addFlashAttribute("mensajeExito", esEdicion
                ? "Persona actualizada correctamente"
                : "Persona registrada correctamente");

        return "redirect:/personas";
    }

    // editar
    @GetMapping("/personas/editar/{id}")
    public String editar(@PathVariable Long id, Model model){
        Persona persona = personaService.buscarPorId(id).orElseThrow();
        model.addAttribute("persona", persona);
        model.addAttribute("tituloFormulario", "Editar persona");
        model.addAttribute("modulo", "personas");
        return "personas/formulario";
    }

    // borrar
    @GetMapping("/personas/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes){
        personaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Persona eliminada correctamente");
        return "redirect:/personas";
    }

    /*
     * Alta rapida de cliente desde el modal "+" del formulario de pedido
     * (ver pedidos/formulario.html). A diferencia de /personas/guardar,
     * este endpoint no redirige a ninguna vista: responde JSON para que
     * el modal se cierre sin recargar la pagina y el cliente recien
     * creado quede seleccionado de inmediato en el combobox.
     */
    @PostMapping("/personas/rapido")
    @ResponseBody
    public ResponseEntity<?> guardarRapido(@Valid @RequestBody Persona persona, BindingResult result) {

        if (result.hasErrors()) {
            // mismo formato de error para todos los campos: {campo: mensaje},
            // asi el JS puede mostrar cada mensaje junto a su input
            Map<String, String> errores = new LinkedHashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            Persona guardada = personaService.guardar(persona);

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("id", guardada.getId());
            respuesta.put("nombre", guardada.getNombres() + " " + guardada.getApellidos());

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException ex) {
            // documento de identidad duplicado (ver PersonaServiceImpl.guardar)
            return ResponseEntity.badRequest().body(Map.of("documentoIdentidad", ex.getMessage()));
        }
    }
}