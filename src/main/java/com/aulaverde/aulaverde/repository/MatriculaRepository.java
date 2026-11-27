package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.Matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    List<Matricula> findByCursoId(Integer cursoId);

    List<Matricula> findByEstudianteId(Integer estudianteId);

    Optional<Matricula> findByEstudianteIdAndCursoId(
        Integer estudianteId, Integer cursoId
    );

    boolean existsByEstudianteIdAndCursoId(
        Integer estudianteId, Integer cursoId
    );
}