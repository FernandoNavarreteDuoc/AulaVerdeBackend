package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.MatriculaDTO;
import com.aulaverde.aulaverde.DTO.CursoDTO;
import com.aulaverde.aulaverde.service.MatriculaService;
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
@RequestMapping("/api/matriculas")
@Tag(name = "Matrículas", description = "Gestión de matrículas de estudiantes en cursos")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private UsuarioService usuarioService;

    // Listar todas las matrículas (bloqueado)
    @Operation(summary = "Listar todas las matrículas")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<MatriculaDTO>> listar() {

        return ResponseEntity.ok(matriculaService.listar());
    }

    // Listar matrículas de un curso
    @Operation(summary = "Listar estudiantes matriculados en un curso propio")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<MatriculaDTO>> listarPorCurso(
            @PathVariable Integer cursoId,
            Principal principal) {

        // Comprobar propietario del curso
        if (!esPropietarioCurso(cursoId, principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                matriculaService.listarPorCurso(cursoId));
    }

    // Listar matrículas de un estudiante
    @Operation(summary = "Consultar matrículas personales")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<MatriculaDTO>> listarPorEstudiante(
            @PathVariable Integer estudianteId,
            Principal principal) {

        // Comprobar identidad del estudiante
        if (!esUsuario(estudianteId, principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                matriculaService.listarPorEstudiante(estudianteId));
    }

    // Buscar matrícula por ID
    @Operation(summary = "Consultar una matrícula autorizada")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/{id}")
    public ResponseEntity<MatriculaDTO> buscarPorId(
            @PathVariable Integer id,
            Principal principal) {

        MatriculaDTO matricula =
                matriculaService.buscarPorId(id);

        // Comprobar estudiante o propietario del curso
        if (!esUsuario(matricula.getEstudianteId(), principal)
                && !esPropietarioCurso(
                        matricula.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(matricula);
    }

    // Matricular estudiante
    @Operation(summary = "Matricular un estudiante en un curso propio")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<MatriculaDTO> guardar(
            @RequestBody MatriculaDTO dto,
            Principal principal) {

        // Comprobar que se indique un curso
        if (dto.getCursoId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Comprobar propietario del curso
        if (!esPropietarioCurso(dto.getCursoId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matriculaService.guardar(dto));
    }

    // Activar matrícula
    @Operation(summary = "Activar una matrícula")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id,
            Principal principal) {

        MatriculaDTO matricula =
                matriculaService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietarioCurso(
                matricula.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        matriculaService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar matrícula
    @Operation(summary = "Desactivar una matrícula")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id,
            Principal principal) {

        MatriculaDTO matricula =
                matriculaService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietarioCurso(
                matricula.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        matriculaService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    // Comprobar identidad del usuario autenticado
    private boolean esUsuario(
            Integer usuarioId,
            Principal principal) {

        return usuarioService.buscarPorId(usuarioId)
                .getCorreo()
                .equals(principal.getName());
    }

    // Comprobar propietario del curso
    private boolean esPropietarioCurso(
            Integer cursoId,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(cursoId);

        return esUsuario(curso.getUsuarioId(), principal);
    }
}