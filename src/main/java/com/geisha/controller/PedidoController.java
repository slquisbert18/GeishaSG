package com.geisha.controller;

import com.geisha.entity.Pedido;
import com.geisha.security.UsuarioDetails;
import com.geisha.service.PedidoService;
import com.geisha.service.PersonaService;
import com.geisha.service.TramiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final PersonaService personaService;
    private final TramiteService tramiteService;

    // listar
    @GetMapping("/pedidos")
    public String listar(Model model) {

        model.addAttribute("pedidos", pedidoService.listarTodos());
        model.addAttribute("modulo", "pedidos");

        return "pedidos/listar";
    }

    // nuevo
    @GetMapping("/pedidos/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("pedido", new Pedido());

        // Clientes disponibles
        model.addAttribute("clientes", personaService.listarClientes());

        // Trámites disponibles
        model.addAttribute("tramites", tramiteService.listarTodos());

        model.addAttribute("tituloFormulario", "Nuevo pedido");
        model.addAttribute("modulo", "pedidos");

        return "pedidos/formulario";
    }

    // editar
    @GetMapping("/pedidos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Pedido pedido = pedidoService.buscarPorId(id).orElseThrow();

        model.addAttribute("pedido", pedido);

        model.addAttribute("clientes", personaService.listarClientes());

        model.addAttribute("tramites", tramiteService.listarTodos());

        model.addAttribute("tituloFormulario", "Editar pedido");
        model.addAttribute("modulo", "pedidos");

        return "pedidos/formulario";
    }

    // guardar
    @PostMapping("/pedidos/guardar")
    public String guardar(@ModelAttribute Pedido pedido,
                          @AuthenticationPrincipal UsuarioDetails usuarioDetails,
                          RedirectAttributes redirectAttributes) {

        boolean esNuevo = pedido.getId() == null;

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
            Pedido pedidoExistente = pedidoService.buscarPorId(pedido.getId()).orElseThrow();
            pedido.setEmpleado(pedidoExistente.getEmpleado());
        }

        pedidoService.guardar(pedido);

        redirectAttributes.addFlashAttribute("mensajeExito",
                esNuevo
                        ? "Pedido registrado correctamente"
                        : "Pedido actualizado correctamente"
        );

        return "redirect:/pedidos";
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
}