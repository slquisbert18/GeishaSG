package com.geisha.controller;

import com.geisha.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PersonaController {
    private final PersonaService personaSerive;

    //@GetMapping("/personas")
    //public String listar(Model model, @RequestParam)
}
