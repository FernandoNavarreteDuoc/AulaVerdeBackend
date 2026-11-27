package com.aulaverde.aulaverde.repository;

import com.aulaverde.aulaverde.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepository
        extends JpaRepository<Documento, Integer> {

    List<Documento> findByUsuarioId(Integer usuarioId);
}