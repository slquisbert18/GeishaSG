package com.geisha.repository;

import com.geisha.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    // devuelve los registros que coincidan con 'nombre'
    List<Persona> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombre, String apellido);

    Optional<Persona> findByDocumentoIdentidad(String ci);

    // verifica si ya existe una persona con el mismo nombre
    boolean existsByDocumentoIdentidad(String nombre);

    @Query("""
    SELECT p
    FROM Persona p
    WHERE NOT EXISTS (
        SELECT u
        FROM Usuario u
        WHERE u.persona = p
    )
    AND (
        LOWER(p.nombres) LIKE LOWER(CONCAT('%', :buscar, '%'))
        OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :buscar, '%'))
    )
""")
    List<Persona> buscarClientes(@Param("buscar") String buscar);

    @Query("""
        SELECT p
        FROM Persona p
        WHERE NOT EXISTS (
            SELECT u
            FROM Usuario u
            WHERE u.persona = p
        )
    """)
    List<Persona> listarClientes();


}
