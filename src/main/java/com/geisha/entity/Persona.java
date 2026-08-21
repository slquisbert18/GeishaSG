package com.geisha.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Este espacio es requerido")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Este espacio es requerido")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @Size(max=20, message="El teléfono no puede superar los 20 caracteres.")
    @Pattern(regexp = "^[0-9]*$", message = "El teléfono solo puede contener caracteres numéricos.")
    @Column(length = 20)
    private String telefono;

    @Email(message="Ingrese un correo electrónico válido.")
    @Column(length = 120)
    private String correo;


    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Size(max=30, message="El documento de identidad no puede superar los 30 caracteres.")
    @Column(length = 30)
    private String documentoIdentidad;
}
