package com.geisha.repository;

import com.geisha.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // pedidos cuyo cliente coincide con el texto buscado (nombre o apellido)
    @Query("""
        SELECT p
        FROM Pedido p
        JOIN p.cliente c
        WHERE LOWER(c.nombres) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :buscar, '%'))
        """)
    List<Pedido> buscarPorCliente(@Param("buscar") String buscar);
}