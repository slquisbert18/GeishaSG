package com.geisha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * Controlador temporal para visualizar la plantilla base.
 *
 * Más adelante la página de inicio mostrará información
 * como pedidos recientes, notificaciones, estadísticas, etc.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio(Model model){

        model.addAttribute("title", "Inicio");
        model.addAttribute("modulo","inicio");

        return "home/inicio";

    }

}