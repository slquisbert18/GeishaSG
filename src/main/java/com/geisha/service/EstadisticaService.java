package com.geisha.service;

import com.geisha.repository.DetalleTrabajoRepository;
import com.geisha.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EstadisticaService {

    private final PedidoRepository pedidoRepository;
    private final DetalleTrabajoRepository detalleTrabajoRepository;

    // cuantos elementos como maximo se muestran en los rankings (tramites
    // e instituciones), para no saturar el grafico si hay muchos
    private static final int TOP_N = 10;

    // monto total vendido por mes: {"2026-06": 450.00, "2026-07": 620.00, ...}
    public Map<String, Object> ventasPorMes() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        for (Object[] fila : pedidoRepository.ventasPorMes()) {
            resultado.put((String) fila[0], fila[1]);
        }
        return resultado;
    }

    // trabajos por tramite, ya recortado a los TOP_N mas solicitados
    public Map<String, Object> tramitesMasSolicitados() {
        return aMapaLimitado(detalleTrabajoRepository.contarPorTramite());
    }

    // trabajos por institucion, ya recortado a las TOP_N con mas demanda
    public Map<String, Object> institucionesConMasDemanda() {
        return aMapaLimitado(detalleTrabajoRepository.contarPorInstitucion());
    }

    // convierte List<Object[]> = [ [nombre, cantidad], ... ] en un mapa
    // ordenado, quedandose solo con los primeros TOP_N (las consultas ya
    // vienen ordenadas de mayor a menor cantidad)
    private Map<String, Object> aMapaLimitado(java.util.List<Object[]> filas) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        filas.stream()
                .limit(TOP_N)
                .forEach(fila -> resultado.put((String) fila[0], fila[1]));
        return resultado;
    }
}