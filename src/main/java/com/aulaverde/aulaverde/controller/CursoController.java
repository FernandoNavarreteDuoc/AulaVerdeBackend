package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.CursoDTO;
import com.aulaverde.aulaverde.service.CursoService;
import com.aulaverde.aulaverde.service.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Gestión de cursos académicos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private UsuarioService usuarioService;

    // Listar todos los cursos (bloqueado temporalmente)
    @Operation(summary = "Listar todos los cursos")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<CursoDTO>> listar() {

        return ResponseEntity.ok(cursoService.listar());
    }

    // Listar cursos de un usuario
    @Operation(summary = "Listar cursos propios")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CursoDTO>> listarPorUsuario(
            @PathVariable Integer usuarioId,
            Principal principal) {

        // Comprobar propietario
        if (!esPropietario(usuarioId, principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                cursoService.listarPorUsuario(usuarioId));
    }

    // Buscar curso por ID
    @Operation(summary = "Consultar curso propio")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> buscarPorId(
            @PathVariable Integer id,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietario(curso.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(curso);
    }

    // Crear curso
    @Operation(summary = "Crear un curso")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<CursoDTO> guardar(
            @PathVariable Integer usuarioId,
            @RequestBody CursoDTO dto,
            Principal principal) {

        // Comprobar que el usuario cree cursos a su nombre
        if (!esPropietario(usuarioId, principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cursoService.guardar(dto, usuarioId));
    }

    // Actualizar curso
    @Operation(summary = "Actualizar información de un curso")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody CursoDTO dto,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietario(curso.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                cursoService.actualizar(id, dto));
    }

    // Activar curso
    @Operation(summary = "Activar un curso")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietario(curso.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        cursoService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar curso
    @Operation(summary = "Desactivar un curso")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietario(curso.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        cursoService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    // Comprobar si el usuario autenticado es el propietario
    private boolean esPropietario(
            Integer usuarioId,
            Principal principal) {

        return usuarioService.buscarPorId(usuarioId)
                .getCorreo()
                .equals(principal.getName());
    }
}