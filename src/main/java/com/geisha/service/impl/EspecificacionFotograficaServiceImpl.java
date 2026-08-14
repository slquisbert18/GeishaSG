package com.geisha.service.impl;

import com.geisha.entity.EspecificacionFotografica;
import com.geisha.repository.EspecificacionFotograficaRepository;
import com.geisha.service.EspecificacionFotograficaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EspecificacionFotograficaServiceImpl implements EspecificacionFotograficaService {
    private final EspecificacionFotograficaRepository efRepository;

    @Override
    public List<EspecificacionFotografica> listarTodos(){
        return efRepository.findAll();
    }

    @Override
    public Optional<EspecificacionFotografica> buscarPorId(Long id){
        return efRepository.findById(id);
    }

    @Override
    public EspecificacionFotografica guardar(EspecificacionFotografica especificacion){
        return efRepository.save(especificacion);
    }

    @Override
    public void eliminar(Long id){
        efRepository.deleteById(id);
    }

    @Override
    public Optional<EspecificacionFotografica> buscarPorTramite(Long tramiteId){
        return efRepository.findByTramiteId(tramiteId);
    }
}
