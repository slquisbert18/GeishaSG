package com.geisha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_trabajo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "precio_servicio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioServicio;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Pedido al que pertenece este trabajo
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    // Trámite realizado
    @ManyToOne
    @JoinColumn(name = "id_tramite", nullable = false)
    private Tramite tramite;
}