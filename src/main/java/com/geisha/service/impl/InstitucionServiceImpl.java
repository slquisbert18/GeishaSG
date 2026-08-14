package com.geisha.service.impl;

import com.geisha.entity.Institucion;
import com.geisha.exception.NombreDuplicadoException;
import com.geisha.repository.InstitucionRepository;
import com.geisha.service.InstitucionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
/*
 * Indica a Spring que esta clase implementa un servicio.
 * Spring creará automáticamente una instancia (Bean) y la administrará.
 */
public class InstitucionServiceImpl implements InstitucionService {

    private final InstitucionRepository institucionRepository;

    /*
     * Inyección de dependencias mediante constructor.
     *
     * Spring detecta este constructor e inyecta automáticamente
     * una instancia de InstitucionRepository.
     *
     */
    public InstitucionServiceImpl(InstitucionRepository institucionRepository) {
        this.institucionRepository = institucionRepository;
    }

    @Override
    public List<Institucion> listarTodas() {
        // Obtiene todas las instituciones de la base de datos.
        return institucionRepository.findAll();
    }

    @Override
    public Optional<Institucion> buscarPorId(Long id) {
        // Busca una institución por su identificador.
        return institucionRepository.findById(id);
    }

    @Override
    public List<Institucion> buscarPorNombre(String nombre){
        return institucionRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Institucion guardar(Institucion institucion){
        // Verificamos si ya existe una institución con el mismo nombre
        Optional<Institucion> existente = institucionRepository.findByNombreIgnoreCase(institucion.getNombre());

        // Si encontramos una institución significa que existe duplicado
        // debemos verificar que no sea el mismo registro que estamos editando.
        if(existente.isPresent() && !existente.get().getId().equals(institucion.getId())){
            throw new NombreDuplicadoException("nombre", "Ya existe una institución con ese nombre");
        }
        return institucionRepository.save(institucion);
    }

    @Override
    public void eliminar(Long id) {
        // Elimina la institución cuyo id fue recibido.
        institucionRepository.deleteById(id);
    }
}