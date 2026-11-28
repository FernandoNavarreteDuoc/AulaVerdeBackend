package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.CursoDTO;
import com.aulaverde.aulaverde.service.CursoService;
import com.aulaverde.aulaverde.service.UsuarioService;
import com.aulaverde.aulaverde.service.MatriculaService;

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

    @Autowired
    private MatriculaService matriculaService;

    // Listar todos los cursos (bloqueado)
    @Operation(summary = "Listar todos los cursos")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<CursoDTO>> listar() {

        return ResponseEntity.ok(cursoService.listar());
    }

    // Listar cursos de un docente o instructor
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

    // Listar cursos de un estudiante matriculado
    @Operation(summary = "Listar cursos en los que está matriculado el estudiante")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<CursoDTO>> listarPorEstudiante(
            @PathVariable Integer estudianteId,
            Principal principal) {

        // Comprobar identidad del estudiante
        if (!esPropietario(estudianteId, principal)) {
            return ResponseEntity.status(403).build();
        }

        // Obtener cursos con matrículas activas
        List<CursoDTO> cursos = matriculaService
                .listarPorEstudiante(estudianteId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                .map(m -> cursoService.buscarPorId(m.getCursoId()))
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .toList();

        return ResponseEntity.ok(cursos);
    }

    // Buscar curso por ID
    @Operation(summary = "Consultar un curso propio o matriculado")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> buscarPorId(
            @PathVariable Integer id,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(id);

        // Permitir acceso al propietario del curso
        if (esPropietario(curso.getUsuarioId(), principal)) {
            return ResponseEntity.ok(curso);
        }

        // Comprobar que el curso esté activo
        if (!Boolean.TRUE.equals(curso.getActivo())) {
            return ResponseEntity.status(403).build();
        }

        // Comprobar matrícula activa del estudiante
        if (!estaMatriculado(id, principal)) {
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

    // Comprobar identidad del usuario autenticado
    private boolean esPropietario(
            Integer usuarioId,
            Principal principal) {

        return usuarioService.buscarPorId(usuarioId)
                .getCorreo()
                .equals(principal.getName());
    }

    // Comprobar si el estudiante tiene una matrícula activa
    private boolean estaMatriculado(
            Integer cursoId,
            Principal principal) {

        return matriculaService.listarPorCurso(cursoId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                .anyMatch(m ->
                        esPropietario(
                                m.getEstudianteId(), principal));
    }
}