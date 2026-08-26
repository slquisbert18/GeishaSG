package com.geisha.service;

import com.geisha.entity.Pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoService {

    List<Pedido> listarTodos();

    Optional<Pedido> buscarPorId(Long id);

    Pedido guardar(Pedido pedido);

    void eliminar(Long id);

    /*
     * Filtro combinado del listado de pedidos. "fecha" y "buscar" son
     * ambos opcionales (null = sin ese filtro): con fecha se limita a
     * los pedidos registrados ese dia; con buscar, a los que coincidan
     * con el nombre/apellido del cliente.
     */
    List<Pedido> filtrar(LocalDate fecha, String buscar);

    List<Pedido> historialCliente(Long clienteId);
}