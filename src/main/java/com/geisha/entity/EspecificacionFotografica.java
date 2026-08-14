package com.geisha.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "especificacion_fotografica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EspecificacionFotografica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private BigDecimal ancho;
    private BigDecimal alto;
    private Short cantidad;

    @Column(name="color_fondo")
    private String colorFondo;

    @Column(name="tipo_papel")
    private String tipoPapel;

    @Column(name="codigo_rgb")
    private String codigoRgb;

    @Column(name="codigo_hex")
    private String codigoHex;

    private Short resolucion;

    private BigDecimal margen;

    private String observaciones;

    // cada especificacion pertenece a un tramite
    // la columna id_tramite de la bd sera administrada por JPA
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="id_tramite", nullable = false)
    @JsonIgnore
    private Tramite tramite;
}

