package com.geisha.service.impl;
import com.geisha.entity.Tramite;
import com.geisha.exception.NombreDuplicadoException;
import com.geisha.repository.EspecificacionFotograficaRepository;
import com.geisha.repository.TramiteRepository;
import com.geisha.service.TramiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TramiteServiceImpl implements TramiteService {

    private final TramiteRepository tramiteRepository;
    private final EspecificacionFotograficaRepository efRepository;

    @Override
    public List<Tramite> listarTodos() {
        return tramiteRepository.findAll();
    }

    @Override
    public Optional<Tramite> buscarPorId(Long id) {
        return tramiteRepository.findById(id);
    }

    @Override
    public Tramite guardar(Tramite tramite) {

        if (tramiteRepository.existsByNombreIgnoreCase(tramite.getNombre())) {

            if (tramite.getId() == null) {
                throw new RuntimeException("Ya existe un trámite con ese nombre");
            }

            Tramite existente = tramiteRepository.findById(tramite.getId()).orElse(null);

            if (existente == null || !existente.getNombre().equalsIgnoreCase(tramite.getNombre())) {
                throw new NombreDuplicadoException(
                        "nombre",
                        "Ya existe un trámite con ese nombre."
                );
            }
        }

        return tramiteRepository.save(tramite);
    }

    @Override
    public void eliminar(Long id) {
        // si un tramite tiene especificacion asociada, hay que borrarla primero
        efRepository.findByTramiteId(id).ifPresent(efRepository::delete);
        tramiteRepository.deleteById(id);
    }

    @Override
    public List<Tramite> buscarPorInstitucion(String nombreInstitucion) {
        return tramiteRepository.findByInstitucion_NombreContainingIgnoreCase(nombreInstitucion);
    }
}