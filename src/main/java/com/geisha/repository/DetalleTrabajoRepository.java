package com.geisha.repository;

import com.geisha.entity.DetalleTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleTrabajoRepository extends JpaRepository<DetalleTrabajo, Long> {
}