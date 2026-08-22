package com.geisha.security;

import com.geisha.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security no sabe nada de nuestra entidad Usuario, solo entiende
 * el contrato UserDetails (username, password, authorities, si esta
 * habilitado, etc). Esta clase "envuelve" un Usuario y traduce sus datos
 * a ese contrato, sin necesidad de duplicar campos en otra tabla.
 *
 * Ademas exponemos el Usuario original (getUsuario()) para poder llegar
 * facilmente hasta su Persona asociada desde cualquier controlador,
 * por ejemplo para saber quien esta logueado al crear un pedido.
 */
@Getter
public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security espera los roles con el prefijo "ROLE_"
        // (asi los reconocen hasRole()/sec:authorize="hasRole(...)").
        // En BD el rol se guarda simple: "ADMINISTRADOR" o "TRABAJADOR".
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    }

    @Override
    public String getPassword() {
        // hash bcrypt guardado en la columna contrasena
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getNombreUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // usamos la columna "activo" para poder desactivar un usuario
        // sin borrarlo (por ejemplo, un trabajador que ya no trabaja aqui)
        return Boolean.TRUE.equals(usuario.getActivo());
    }
}