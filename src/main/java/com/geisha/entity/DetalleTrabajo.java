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

    /*
     * Ruta del archivo con el trabajo final ya realizado
     * Se guarda tal como el usuario la escribe o la
     * elige con el explorador nativo (ver PedidoController), asi que
     * puede ser una ruta absoluta de Windows como
     * "C:\Fotos\pedido15_carnet.jpg". Al ser una ruta libre (no un
     * archivo gestionado por el sistema), esta funcion depende de que
     * el archivo siga existiendo en esa ubicacion en el equipo donde
     * corre el servidor.
     */
    @Column(name = "ruta", length = 500)
    private String ruta;
}