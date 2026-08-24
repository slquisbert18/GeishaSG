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

    // historial de pedidos de un cliente puntual (para la notificacion de cumpleanos)
    List<Pedido> findByClienteIdOrderByFechaRegistroDesc(Long clienteId);

    // monto total vendido, agrupado por mes ("2026-08", etc). TO_CHAR es
    // especifico de PostgreSQL (el proyecto ya esta atado a ese motor,
    // ver application.properties), asi que se usa directo via FUNCTION().
    // Cada Object[] es [periodo (String), montoTotal (BigDecimal)]
    @Query("""
        SELECT FUNCTION('TO_CHAR', p.fechaRegistro, 'YYYY-MM'), SUM(p.montoTotal)
        FROM Pedido p
        GROUP BY FUNCTION('TO_CHAR', p.fechaRegistro, 'YYYY-MM')
        ORDER BY FUNCTION('TO_CHAR', p.fechaRegistro, 'YYYY-MM')
        """)
    List<Object[]> ventasPorMes();
}