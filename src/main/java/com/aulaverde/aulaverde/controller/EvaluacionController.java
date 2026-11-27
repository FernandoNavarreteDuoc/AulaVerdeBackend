package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.EvaluacionDTO;
import com.aulaverde.aulaverde.service.EvaluacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    // Listar todas las evaluaciones
    @GetMapping
    public ResponseEntity<List<EvaluacionDTO>> listar() {

        return ResponseEntity.ok(evaluacionService.listar());
    }

    // Listar evaluaciones de un curso
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<EvaluacionDTO>> listarPorCurso(
            @PathVariable Integer cursoId) {

        return ResponseEntity.ok(
                evaluacionService.listarPorCurso(cursoId));
    }

    // Buscar evaluación por ID
    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                evaluacionService.buscarPorId(id));
    }

    // Crear evaluación
    @PostMapping
    public ResponseEntity<EvaluacionDTO> guardar(
            @RequestBody EvaluacionDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(evaluacionService.guardar(dto));
    }

    // Actualizar evaluación
    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody EvaluacionDTO dto) {

        return ResponseEntity.ok(
                evaluacionService.actualizar(id, dto));
    }

    // Activar evaluación
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id) {

        evaluacionService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar evaluación
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id) {

        evaluacionService.desactivar(id);

        return ResponseEntity.noContent().build();
    }
}