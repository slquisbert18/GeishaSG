package com.geisha.service;

import com.geisha.entity.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaService {
    List<Persona> listarTodos();
    Optional<Persona> buscarPorId(Long id);
    Optional<Persona> buscarPorDocumentoIdentidad(String ci);
    Persona guardar(Persona persona);
    void eliminar(Long id);
    List<Persona> buscarPorNombreOApellido(String nombre);
}
