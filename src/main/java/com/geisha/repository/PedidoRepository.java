package com.geisha.repository;

import com.geisha.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /*
     * Consulta unica y flexible para el listado de pedidos: cada
     * criterio es opcional (si el parametro llega null, esa condicion se
     * ignora gracias al "OR :param IS NULL"). Esto permite combinar el
     * filtro de fecha con la busqueda por cliente, o usar solo uno de
     * los dos, sin escribir una consulta distinta por cada combinacion.
     *
     * fechaInicio/fechaFin delimitan un dia completo (00:00 a 00:00 del
     * dia siguiente); "buscar" compara contra nombre o apellido del cliente.
     */
    @Query("""
        SELECT p
        FROM Pedido p
        JOIN p.cliente c
        WHERE (CAST(:fechaInicio AS timestamp) IS NULL OR p.fechaRegistro >= :fechaInicio)
          AND (CAST(:fechaFin AS timestamp) IS NULL OR p.fechaRegistro < :fechaFin)
          AND (:buscar IS NULL
               OR LOWER(c.nombres) LIKE LOWER(CONCAT('%', CAST(:buscar AS string), '%'))
               OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', CAST(:buscar AS string), '%')))
        ORDER BY p.fechaRegistro DESC
        """)
    List<Pedido> filtrar(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("buscar") String buscar
    );

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