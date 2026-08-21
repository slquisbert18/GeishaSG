package com.geisha.service.impl;

import com.geisha.entity.DetalleTrabajo;
import com.geisha.repository.DetalleTrabajoRepository;
import com.geisha.service.DetalleTrabajoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DetalleTrabajoServiceImpl implements DetalleTrabajoService {

    private final DetalleTrabajoRepository detalleTrabajoRepository;

    @Override
    public List<DetalleTrabajo> listarTodos() {
        return detalleTrabajoRepository.findAll();
    }

    @Override
    public Optional<DetalleTrabajo> buscarPorId(Long id) {
        return detalleTrabajoRepository.findById(id);
    }

    @Override
    public DetalleTrabajo guardar(DetalleTrabajo detalleTrabajo) {
        return detalleTrabajoRepository.save(detalleTrabajo);
    }

    @Override
    public void eliminar(Long id) {
        detalleTrabajoRepository.deleteById(id);
    }
}