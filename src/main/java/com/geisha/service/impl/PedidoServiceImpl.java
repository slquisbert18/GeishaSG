package com.geisha.service.impl;

import com.geisha.entity.Pedido;
import com.geisha.repository.PedidoRepository;
import com.geisha.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    @Override
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void eliminar(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    public List<Pedido> filtrar(LocalDate fecha, String buscar) {

        // un LocalDate ("2026-08-25") no se puede comparar directo contra
        // fecha_registro (que es timestamp): se convierte al rango
        // [00:00 del dia, 00:00 del dia siguiente)
        LocalDateTime fechaInicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fechaFin = (fecha != null) ? fecha.plusDays(1).atStartOfDay() : null;

        String textoBusqueda = (buscar != null && !buscar.isBlank()) ? buscar : null;

        return pedidoRepository.filtrar(fechaInicio, fechaFin, textoBusqueda);
    }

    @Override
    public List<Pedido> historialCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaRegistroDesc(clienteId);
    }
}