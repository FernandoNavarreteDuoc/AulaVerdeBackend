package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.RubricaDTO;
import com.aulaverde.aulaverde.service.RubricaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rubricas")
public class RubricaController {

    @Autowired
    private RubricaService rubricaService;

    // Listar todas las rúbricas
    @GetMapping
    public ResponseEntity<List<RubricaDTO>> listar() {

        return ResponseEntity.ok(rubricaService.listar());
    }

    // Listar rúbricas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RubricaDTO>> listarPorUsuario(
            @PathVariable Integer usuarioId) {

        return ResponseEntity.ok(
                rubricaService.listarPorUsuario(usuarioId));
    }

    // Buscar rúbrica por ID
    @GetMapping("/{id}")
    public ResponseEntity<RubricaDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                rubricaService.buscarPorId(id));
    }

    // Crear rúbrica
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<RubricaDTO> guardar(
            @PathVariable Integer usuarioId,
            @RequestBody RubricaDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rubricaService.guardar(dto, usuarioId));
    }

    // Activar rúbrica
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id) {

        rubricaService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar rúbrica
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id) {

        rubricaService.desactivar(id);

        return ResponseEntity.noContent().build();
    }
}