package com.geisha.repository;

import com.geisha.entity.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Indica que esta interfaz pertenece a la capa de acceso a datos.
public interface InstitucionRepository extends JpaRepository<Institucion, Long> {

    /*
     * JpaRepository ya implementa automáticamente operaciones como:
     *
     * save()       -> Guardar o actualizar.
     * findById()   -> Buscar por id.
     * findAll()    -> Obtener todos los registros.
     * delete()     -> Eliminar.
     * count()      -> Contar registros.
     */

    /*
     * Busca una institución con el nombre exacto.
     *
     * Se utilizará para verificar duplicados
     * antes de guardar.
     */
    Optional<Institucion> findByNombreIgnoreCase(String nombre);

    /*
     * Busca instituciones cuyo nombre contenga
     * el texto recibido ignorando mayúsculas/minúsculas.
     *
     * Ejemplo:
     *
     * Entrada:
     * "uni"
     *
     * Encuentra:
     * Universidad Mayor de San Andrés
     * Universidad Católica Boliviana
     *
     * findByNombreContainingIgnoreCase = WHERE nombre ILIKE '%texto%'
     */
    List<Institucion> findByNombreContainingIgnoreCase(String nombre);

}