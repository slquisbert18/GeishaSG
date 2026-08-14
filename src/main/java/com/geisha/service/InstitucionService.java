package com.geisha.service;

import com.geisha.entity.Institucion;

import java.util.List;
import java.util.Optional;

/*
 * Un Service representa la lógica de negocio de una entidad.
 *
 * El Controller nunca debería comunicarse directamente con el Repository.
 * En su lugar, utiliza el Service, que actúa como intermediario.
 *
 * Esto permite modificar la lógica de negocio sin afectar al Controller.
 */
public interface InstitucionService {

    // Devuelve todas las instituciones registradas.
    List<Institucion> listarTodas();

    // Busca una institución por su id.
    Optional<Institucion> buscarPorId(Long id);

    // Busca instituciones por nombre
    List<Institucion> buscarPorNombre(String nombre);

    // Guarda una nueva institución o actualiza una existente.
    Institucion guardar(Institucion institucion);

    // Elimina una institución por su id.
    void eliminar(Long id);

}