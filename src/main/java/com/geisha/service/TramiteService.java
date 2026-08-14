package com.geisha.service;
import com.geisha.entity.Tramite;

import java.util.List;
import java.util.Optional;

public interface TramiteService {
    // Devuelve todos los trámites registrados
    List<Tramite> listarTodos();

    // Busca un trámite por su id
    Optional<Tramite> buscarPorId(Long id);

    // Guarda un nuevo trámite o actualiza uno existente
    Tramite guardar(Tramite tramite);

    // Elimina un trámite por su id
    void eliminar(Long id);

    // Busca trámites por nombre
    List<Tramite> buscarPorNombre(String nombre);
}
