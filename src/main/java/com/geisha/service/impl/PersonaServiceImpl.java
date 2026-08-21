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
    public List<Persona> listarClientes(){
        return personaRepository.listarClientes();
    }

    @Override
    public Optional<Persona> buscarPorId(Long id){
        return personaRepository.findById(id);
    }

    @Override
    public Optional<Persona> buscarPorDocumentoIdentidad(String ci){
        return personaRepository.findByDocumentoIdentidad(ci);
    }

    @Override
    public Persona guardar(Persona persona){
        Optional<Persona> personaExistente = personaRepository.findByDocumentoIdentidad(persona.getDocumentoIdentidad());
        if(personaExistente.isPresent()){
            // nueva persona
            if(persona.getId() == null){
                throw new RuntimeException("Ya existe una persona con ese documento de identidad");
            }

            // edicion: verificar que no sea otra persona
            if(!personaExistente.get().getId().equals(persona.getId())){
                throw new RuntimeException("Existe una persona con ese documento de identidad");
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
        return personaRepository.buscarClientes(nombreApellido);
    }
}
