package com.geisha.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity // Indica que esta clase representa una tabla de la base de datos.
@Table(name = "institucion") // Especifica el nombre de la tabla en PostgreSQL.
@Getter // Lombok genera automáticamente los métodos get().
@Setter // Lombok genera automáticamente los métodos set().
@NoArgsConstructor // Constructor vacío requerido por JPA.
@AllArgsConstructor // Constructor con todos los atributos.
@Builder // Permite crear objetos usando el patrón Builder.
public class Institucion {

    @Id // Marca la clave primaria.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // El valor del id es generado automáticamente por PostgreSQL.
    private Long id;

    @NotBlank(message="El nombre es obligatorio.")
    @Size(max=150, message="El nombre no puede superar los 150 caracteres.")
    @Column(nullable = false, unique = true, length = 150)// Mapea la columna con sus restricciones.
    private String nombre;

    @Size(max=250, message="La dirección no puede superar los 250 caracteres.")
    @Column(length = 250)
    private String direccion;

    @Size(max=20, message="El teléfono no puede superar los 20 caracteres.")
    @Pattern(regexp = "^[0-9]*$", message = "El teléfono solo puede contener caracteres numéricos.")
    @Column(length = 20)
    private String telefono;

    @Email(message="Ingrese un correo electrónico válido.")
    @Column(length = 120)
    private String correo;

    @Size(max=120, message="El horario no puede superar los 120 caracteres.")
    @Column(length = 120)
    private String horario;

//    @Pattern(
//            regexp = "^(|https?://)?(www\\.)?([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+[a-zA-Z]{2,}(/.*)?$",
//            message = "El formato de la página web no es válido"
//    )
    @Column(name = "pagina_web", length = 200)
    // "paginaWeb" en Java corresponde a "pagina_web" en la base de datos.
    private String paginaWeb;
}