package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.DetalleCalificacion;

import java.util.List;

public interface DetalleCalificacionRepository extends JpaRepository<DetalleCalificacion, Integer> {

    List<DetalleCalificacion> findByCalificacionId(Integer calificacionId);
}