package com.geisha.repository;

import com.geisha.entity.EspecificacionFotografica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EspecificacionFotograficaRepository extends JpaRepository<EspecificacionFotografica, Long> {
    Optional<EspecificacionFotografica> findByTramiteId(Long tramiteId);
}