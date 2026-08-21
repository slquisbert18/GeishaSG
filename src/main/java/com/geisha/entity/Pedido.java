package com.geisha.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    // Cliente que realiza el pedido
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Persona cliente;

    // Trabajador que registra el pedido
    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Persona empleado;

    // Trabajos incluidos en el pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleTrabajo> detalles = new ArrayList<>();
}