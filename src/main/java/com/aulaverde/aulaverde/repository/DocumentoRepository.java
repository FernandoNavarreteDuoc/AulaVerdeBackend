package com.aulaverde.aulaverde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aulaverde.aulaverde.model.Documento;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

    List<Documento> findByUsuarioId(Integer usuarioId);

    Optional<Documento> findByClaveArchivo(String claveArchivo);
}