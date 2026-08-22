package com.geisha.service.impl;

import com.geisha.dto.TrabajadorForm;
import com.geisha.entity.Persona;
import com.geisha.entity.Usuario;
import com.geisha.repository.PersonaRepository;
import com.geisha.repository.UsuarioRepository;
import com.geisha.service.TrabajadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorForm> listarTodos() {
        return usuarioRepository.findAll().stream().map(usuario -> new TrabajadorForm(
                usuario.getPersona(),
                usuario
        )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrabajadorForm> buscar(String buscar) {
        return usuarioRepository.buscar(buscar).stream().map(usuario -> new TrabajadorForm(
                usuario.getPersona(),
                usuario
        )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TrabajadorForm buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Trabajador no encontrado."));

        return new TrabajadorForm(usuario.getPersona(), usuario);
    }

    @Override
    @Transactional
    public void guardar(TrabajadorForm trabajadorForm) {

        Persona persona = personaRepository.save(trabajadorForm.getPersona());

        Usuario usuario = trabajadorForm.getUsuario();

        usuario.setPersona(persona);

        // verificamos nombre de usuario duplicado
        Optional<Usuario> usuarioExistenteNombreUsuario = usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario());
        if(usuarioExistenteNombreUsuario.isPresent() && !usuarioExistenteNombreUsuario.get().getId().equals(usuario.getId())){
            throw new RuntimeException("Ya existe ese nombre de usuario");
        }

        // si el usuario existe, su contrasenia persiste
        if(usuario.getId() != null){
            Usuario usuarioExistente = usuarioRepository.findById(usuario.getId()).orElseThrow(()->
                    new RuntimeException("Trabajador no encontrado"));
            usuario.setContrasena(usuarioExistente.getContrasena());
        }
        else{
            // nuevo trabajador
            if(usuario.getContrasena() == null || usuario.getContrasena().isBlank()){
                throw new RuntimeException("La contraseña es obligatoria");
            }

            // nunca se guarda en texto plano: se guarda el hash bcrypt.
            // Con esto el login (via UsuarioDetails.getPassword()) puede
            // comparar el hash guardado contra un nuevo hash del intento
            // de acceso, sin que la contraseña real quede expuesta en BD.
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }

        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Trabajador no encontrado."));

        usuarioRepository.delete(usuario);
        personaRepository.delete(usuario.getPersona());
    }
}