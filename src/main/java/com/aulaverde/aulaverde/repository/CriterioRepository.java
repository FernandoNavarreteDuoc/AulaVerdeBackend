package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.Criterio;

import java.util.List;

public interface CriterioRepository extends JpaRepository<Criterio, Integer> {

    List<Criterio> findByRubricaIdOrderByOrdenAsc(Integer rubricaId);
}