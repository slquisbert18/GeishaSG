package com.geisha.service;

import com.geisha.entity.EspecificacionFotografica;

import java.util.List;
import java.util.Optional;

public interface EspecificacionFotograficaService {
    List<EspecificacionFotografica> listarTodos();
    Optional<EspecificacionFotografica> buscarPorId(Long id);
    EspecificacionFotografica guardar(EspecificacionFotografica especificacionFotografica);
    void eliminar(Long id);
    Optional<EspecificacionFotografica> buscarPorTramite(Long tramiteId);
}
