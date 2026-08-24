package com.geisha.controller;

import com.geisha.dto.CumpleanosProximo;
import com.geisha.entity.Pedido;
import com.geisha.service.NotificacionService;
import com.geisha.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final PedidoService pedidoService;

    // ventana de dias que se usa en la pagina completa (mas amplia, para
    // planificar con anticipacion) y en la campanita (mas corta, solo lo
    // realmente inminente)
    private static final int DIAS_PAGINA = 30;
    private static final int DIAS_CAMPANITA = 7;

    // pagina completa de notificaciones
    @GetMapping("/notificaciones")
    @Transactional(readOnly = true) // permite leer pedido.detalles (lazy) al armar el historial de cada cliente
    public String listar(Model model) {

        List<CumpleanosProximo> cumpleanos = notificacionService.obtenerProximos(DIAS_PAGINA);

        // junto a cada cliente se arma su historial de pedidos, para no
        // tener que ir a buscarlo aparte por cada tarjeta en la vista
        Map<Long, List<Pedido>> historialPorCliente = new LinkedHashMap<>();
        for (CumpleanosProximo cp : cumpleanos) {
            historialPorCliente.put(cp.getPersona().getId(), pedidoService.historialCliente(cp.getPersona().getId()));
        }

        model.addAttribute("cumpleanos", cumpleanos);
        model.addAttribute("historialPorCliente", historialPorCliente);
        model.addAttribute("modulo", "notificaciones");

        return "notificaciones/listar";
    }

    // datos livianos para el dropdown de la campanita en el topbar
    @GetMapping("/notificaciones/resumen")
    @ResponseBody
    public List<Map<String, Object>> resumen() {

        return notificacionService.obtenerProximos(DIAS_CAMPANITA).stream()
                .map(cp -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", cp.getPersona().getId());
                    item.put("nombre", cp.getPersona().getNombres() + " " + cp.getPersona().getApellidos());
                    item.put("diasRestantes", cp.getDiasRestantes());
                    return item;
                })
                .toList();
    }
}