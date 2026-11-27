package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.NivelDesempeno;

import java.util.List;

public interface NivelDesempenoRepository extends JpaRepository<NivelDesempeno, Integer> {

    List<NivelDesempeno> findByCriterioIdOrderByOrdenAsc(Integer criterioId);
}