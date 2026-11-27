package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.CursoDTO;
import com.aulaverde.aulaverde.service.CursoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    // Listar todos los cursos
    @GetMapping
    public ResponseEntity<List<CursoDTO>> listar() {

        return ResponseEntity.ok(cursoService.listar());
    }

    // Listar cursos de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CursoDTO>> listarPorUsuario(
            @PathVariable Integer usuarioId) {

        return ResponseEntity.ok(
                cursoService.listarPorUsuario(usuarioId));
    }

    // Buscar curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                cursoService.buscarPorId(id));
    }

    // Crear curso
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<CursoDTO> guardar(
            @PathVariable Integer usuarioId,
            @RequestBody CursoDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cursoService.guardar(dto, usuarioId));
    }

    // Actualizar curso
    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody CursoDTO dto) {

        return ResponseEntity.ok(
                cursoService.actualizar(id, dto));
    }

    // Activar curso
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id) {

        cursoService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar curso
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id) {

        cursoService.desactivar(id);

        return ResponseEntity.noContent().build();
    }
}