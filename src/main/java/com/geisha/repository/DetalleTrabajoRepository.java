package com.geisha.repository;

import com.geisha.entity.DetalleTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleTrabajoRepository extends JpaRepository<DetalleTrabajo, Long> {

    // trabajos agrupados por tramite, del mas solicitado al menos: cada
    // Object[] es [nombreTramite (String), cantidad (Long)]
    @Query("""
        SELECT d.tramite.nombre, COUNT(d)
        FROM DetalleTrabajo d
        GROUP BY d.tramite.nombre
        ORDER BY COUNT(d) DESC
        """)
    List<Object[]> contarPorTramite();

    // lo mismo pero agrupado por la institucion duena de cada tramite
    @Query("""
        SELECT d.tramite.institucion.nombre, COUNT(d)
        FROM DetalleTrabajo d
        GROUP BY d.tramite.institucion.nombre
        ORDER BY COUNT(d) DESC
        """)
    List<Object[]> contarPorInstitucion();
}