package com.geisha.controller;

import com.geisha.service.EstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    @GetMapping("/estadisticas")
    public String dashboard(Model model) {
        model.addAttribute("modulo", "estadisticas");
        return "estadisticas/dashboard";
    }

    // un solo endpoint con los 3 datasets: evita 3 idas y vueltas al
    // servidor cuando la pagina carga los graficos
    @GetMapping("/estadisticas/datos")
    @ResponseBody
    public Map<String, Object> datos() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("ventasPorMes", estadisticaService.ventasPorMes());
        resultado.put("tramitesMasSolicitados", estadisticaService.tramitesMasSolicitados());
        resultado.put("institucionesConMasDemanda", estadisticaService.institucionesConMasDemanda());
        return resultado;
    }
}