package com.geisha.controller;

import com.geisha.entity.Pedido;
import com.geisha.service.PedidoService;
import com.geisha.service.PersonaService;
import com.geisha.service.TramiteService;
import lombok.RequiredArgsConstructor;
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
    public String guardar(@ModelAttribute Pedido pedido, RedirectAttributes redirectAttributes) {

        boolean esNuevo = pedido.getId() == null;
        pedido.setEmpleado(personaService.buscarPorId(1L).orElseThrow());
        // solucion temporal
//        if(esNuevo){
//            pedido.setEmpleado(personaService.buscarPorId(1L).orElseThrow());
//        }

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