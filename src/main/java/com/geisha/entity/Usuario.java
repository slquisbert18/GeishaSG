package com.geisha.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false, length = 20)
    private String rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToOne
    @JoinColumn(name = "id_persona", nullable = false, unique = true)
    private Persona persona;
}