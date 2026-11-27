package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.Calificacion;

import java.util.List;
import java.util.Optional;

public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    List<Calificacion> findByEvaluacionId(Integer evaluacionId);

    List<Calificacion> findByMatriculaId(Integer matriculaId);

    List<Calificacion> findByMatriculaEstudianteId(Integer estudianteId);

    Optional<Calificacion> findByEvaluacionIdAndMatriculaId(
        Integer evaluacionId, Integer matriculaId
    );

    boolean existsByEvaluacionId(Integer evaluacionId);

    boolean existsByEvaluacionIdAndMatriculaId(
        Integer evaluacionId, Integer matriculaId);
}