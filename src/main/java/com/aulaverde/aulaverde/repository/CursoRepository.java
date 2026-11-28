package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.Curso;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Integer> {

    List<Curso> findByUsuarioId(Integer usuarioId);

    List<Curso> findByUsuarioIdAndActivoTrue(Integer usuarioId);
}