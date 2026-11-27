package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.CalificacionDTO;
import com.aulaverde.aulaverde.service.CalificacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    // Listar todas las calificaciones
    @GetMapping
    public ResponseEntity<List<CalificacionDTO>> listar() {

        return ResponseEntity.ok(
                calificacionService.listar());
    }

    // Buscar calificación por ID
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                calificacionService.buscarPorId(id));
    }

    // Listar calificaciones de una evaluación
    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<List<CalificacionDTO>> listarPorEvaluacion(
            @PathVariable Integer evaluacionId) {

        return ResponseEntity.ok(
                calificacionService.listarPorEvaluacion(evaluacionId));
    }

    // Listar calificaciones de una matrícula
    @GetMapping("/matricula/{matriculaId}")
    public ResponseEntity<List<CalificacionDTO>> listarPorMatricula(
            @PathVariable Integer matriculaId) {

        return ResponseEntity.ok(
                calificacionService.listarPorMatricula(matriculaId));
    }

    // Registrar calificación
    @PostMapping
    public ResponseEntity<CalificacionDTO> guardar(
            @RequestBody CalificacionDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(calificacionService.guardar(dto));
    }
}