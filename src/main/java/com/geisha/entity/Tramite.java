package com.geisha.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tramite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 300, message = "La descripción no puede superar los 300 caracteres.")
    @Column(length = 300)
    private String descripcion;

    @NotNull(message = "El precio base es obligatorio.")
    @DecimalMin(value = "0.00", message = "El precio debe ser mayor o igual a 0.")
    @Column(name = "precio_base")
    private BigDecimal precioBase;

    @NotNull(message = "Debe indicar si el trámite está activo.")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    //@NotNull(message = "Debe seleccionar una institución.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion", nullable = false)
    private Institucion institucion;

    @OneToOne(mappedBy = "tramite", fetch = FetchType.LAZY)
    private EspecificacionFotografica especificacionFotografica;
}
