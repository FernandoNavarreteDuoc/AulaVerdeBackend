package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.Evaluacion;

import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {

    List<Evaluacion> findByCursoId(Integer cursoId);

    List<Evaluacion> findByCursoIdAndActivoTrue(Integer cursoId);

    boolean existsByRubricaId(Integer rubricaId);
}