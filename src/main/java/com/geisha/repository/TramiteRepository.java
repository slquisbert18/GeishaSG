package com.geisha.repository;

import com.geisha.entity.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TramiteRepository extends JpaRepository<Tramite, Long> {

    // devuelve los/el nombre de los tramites que coincidan con 'nombre'
    List<Tramite> findByNombreContainingIgnoreCase(String nombre);

    // Verifica si ya existe un trámite con el mismo nombre.
    boolean existsByNombreIgnoreCase(String nombre);

}