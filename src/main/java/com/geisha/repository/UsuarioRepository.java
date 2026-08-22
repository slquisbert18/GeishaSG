package com.geisha.repository;

import com.geisha.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByPersonaId(Long personaId);

    // trabajadores u=cuyo nombre, apellido o nombre de usuario coincide con el texto indicado
    @Query("""
        SELECT u
        FROM Usuario u
        JOIN u.persona p
        WHERE LOWER(p.nombres) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :buscar, '%'))
           OR LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :buscar, '%'))
        """)
    List<Usuario> buscar(@Param("buscar") String buscar);


}