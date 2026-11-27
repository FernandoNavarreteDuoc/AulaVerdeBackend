package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.MatriculaDTO;
import com.aulaverde.aulaverde.service.MatriculaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    // Listar todas las matrículas
    @GetMapping
    public ResponseEntity<List<MatriculaDTO>> listar() {

        return ResponseEntity.ok(matriculaService.listar());
    }

    // Listar matrículas de un curso
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<MatriculaDTO>> listarPorCurso(
            @PathVariable Integer cursoId) {

        return ResponseEntity.ok(
                matriculaService.listarPorCurso(cursoId));
    }

    // Listar matrículas de un estudiante
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<MatriculaDTO>> listarPorEstudiante(
            @PathVariable Integer estudianteId) {

        return ResponseEntity.ok(
                matriculaService.listarPorEstudiante(estudianteId));
    }

    // Buscar matrícula por ID
    @GetMapping("/{id}")
    public ResponseEntity<MatriculaDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                matriculaService.buscarPorId(id));
    }

    // Matricular estudiante
    @PostMapping
    public ResponseEntity<MatriculaDTO> guardar(
            @RequestBody MatriculaDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matriculaService.guardar(dto));
    }

    // Activar matrícula
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id) {

        matriculaService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar matrícula
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id) {

        matriculaService.desactivar(id);

        return ResponseEntity.noContent().build();
    }
}