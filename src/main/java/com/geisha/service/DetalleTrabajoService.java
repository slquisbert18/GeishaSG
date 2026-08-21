package com.geisha.service;

import com.geisha.entity.DetalleTrabajo;

import java.util.List;
import java.util.Optional;

public interface DetalleTrabajoService {

    List<DetalleTrabajo> listarTodos();

    Optional<DetalleTrabajo> buscarPorId(Long id);

    DetalleTrabajo guardar(DetalleTrabajo detalleTrabajo);

    void eliminar(Long id);
}