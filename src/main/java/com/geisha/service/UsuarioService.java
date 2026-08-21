package com.geisha.service;

import com.geisha.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    Optional<Usuario> buscarPorPersonaId(Long personaId);

    Usuario guardar(Usuario usuario);

    void eliminar(Long id);
}