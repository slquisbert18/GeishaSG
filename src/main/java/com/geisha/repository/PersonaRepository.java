package com.geisha.repository;

import com.geisha.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
