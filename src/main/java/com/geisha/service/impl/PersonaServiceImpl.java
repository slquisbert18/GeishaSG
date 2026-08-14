package com.geisha.service.impl;

import com.geisha.entity.Persona;
import com.geisha.repository.PersonaRepository;
import com.geisha.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository personaRepository;

    @Override
    public List<Persona> listarTodos(){
        return personaRepository.findAll();
    }

    @Override
    public Optional<Persona> buscarPorId(Long id){
        return personaRepository.findById(id);
    }

    public Optional<Persona> buscarPorDocumentoIdentidad(String ci){
        return personaRepository.findByDocumentoIdentidad(ci);
    }

    @Override
    public Persona guardar(Persona persona){
        //verificamos que la persona existe
        if(personaRepository.existsByDocumentoIdentidad(persona.getDocumentoIdentidad())){
            // si el id de la persona es nulo se trata de un nuevo registro
            if(persona.getId() == null){
                throw new RuntimeException("Ya existe una persona con ese documento de identidad");
            }

            Persona existente = personaRepository.findById(persona.getId()).orElse(null);

            if(existente == null || !existente.getDocumentoIdentidad().equalsIgnoreCase(persona.getDocumentoIdentidad())){
                throw new RuntimeException("Ya existe otra persona con ese documento de identidad");
            }
        }
        return personaRepository.save(persona);
    }

    @Override
    public void eliminar(Long id){
        personaRepository.deleteById(id);
    }

    @Override
    public List<Persona> buscarPorNombreOApellido(String nombreApellido){
        return personaRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombreApellido, nombreApellido);
    }
}
