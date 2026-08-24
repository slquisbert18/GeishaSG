package com.geisha.service;

import com.geisha.entity.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoService {

    List<Pedido> listarTodos();

    Optional<Pedido> buscarPorId(Long id);

    Pedido guardar(Pedido pedido);

    void eliminar(Long id);

    List<Pedido> buscarPorCliente(String buscar);

    List<Pedido> historialCliente(Long clienteId);
}