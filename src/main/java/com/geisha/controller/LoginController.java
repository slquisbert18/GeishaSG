package com.geisha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Solo muestra el formulario. El POST a esta misma URL /login lo
    // intercepta y procesa Spring Security automaticamente (configurado
    // en SecurityConfig con .loginProcessingUrl("/login")), nunca llega
    // a este metodo.
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}