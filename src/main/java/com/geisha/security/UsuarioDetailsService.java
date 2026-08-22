package com.geisha.security;

import com.geisha.entity.Usuario;
import com.geisha.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService: es el "puente" que Spring Security
 * llama automaticamente cuando alguien intenta loguearse. Recibe el
 * nombre_usuario que escribio en el formulario y debe devolver un
 * UserDetails (o lanzar UsernameNotFoundException si no existe).
 *
 * Spring Security se encarga solo de comparar la contraseña (hash contra
 * hash); nosotros solo entregamos los datos del usuario.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario con ese nombre de usuario"));

        return new UsuarioDetails(usuario);
    }
}