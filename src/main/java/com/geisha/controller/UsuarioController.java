package com.geisha.controller;

import com.geisha.entity.Usuario;
import com.geisha.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // listar trabajadores
//    @GetMapping("/trabajadores")
//    public String listar(Model model) {
//
//        model.addAttribute("trabajadores", usuarioService.listarTodos());
//        model.addAttribute("modulo", "trabajadores");
//
//        return "trabajadores/listar";
//    }

    // nuevo trabajador
//    @GetMapping("/trabajadores/nuevo")
//    public String nuevo(Model model) {
//
//        model.addAttribute("usuario", new Usuario());
//        model.addAttribute("tituloFormulario", "Nuevo trabajador");
//        model.addAttribute("modulo", "trabajadores");
//
//        return "trabajadores/formulario";
//    }

    // editar trabajador
//    @GetMapping("/trabajadores/editar/{id}")
//    public String editar(@PathVariable Long id, Model model) {
//
//        Usuario usuario = usuarioService.buscarPorId(id).orElseThrow();
//
//        model.addAttribute("usuario", usuario);
//        model.addAttribute("tituloFormulario", "Editar trabajador");
//        model.addAttribute("modulo", "trabajadores");
//
//        return "trabajadores/formulario";
//    }

    // eliminar trabajador
//    @GetMapping("/trabajadores/eliminar/{id}")
//    public String eliminar(@PathVariable Long id) {
//
//        usuarioService.eliminar(id);
//
//        return "redirect:/trabajadores";
//    }
}