package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.Rubrica;

import java.util.List;

public interface RubricaRepository extends JpaRepository<Rubrica, Integer> {

    List<Rubrica> findByUsuarioId(Integer usuarioId);

    List<Rubrica> findByUsuarioIdAndActivoTrue(Integer usuarioId);
}