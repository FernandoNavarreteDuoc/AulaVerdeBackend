package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.EvaluacionDTO;
import com.aulaverde.aulaverde.DTO.CursoDTO;
import com.aulaverde.aulaverde.service.EvaluacionService;
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
@RequestMapping("/api/evaluaciones")
@Tag(name = "Evaluaciones", description = "Gestión de evaluaciones académicas")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MatriculaService matriculaService;

    // Listar todas las evaluaciones (bloqueado)
    @Operation(summary = "Listar todas las evaluaciones")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<EvaluacionDTO>> listar() {

        return ResponseEntity.ok(evaluacionService.listar());
    }

    // Listar evaluaciones de un curso
    @Operation(summary = "Listar evaluaciones de un curso autorizado")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<EvaluacionDTO>> listarPorCurso(
            @PathVariable Integer cursoId,
            Principal principal) {

        // Permitir acceso al propietario del curso
        if (esPropietarioCurso(cursoId, principal)) {
            return ResponseEntity.ok(
                    evaluacionService.listarPorCurso(cursoId));
        }

        // Comprobar matrícula activa del estudiante
        if (!estaMatriculado(cursoId, principal)) {
            return ResponseEntity.status(403).build();
        }

        // Mostrar solamente evaluaciones activas
        return ResponseEntity.ok(
                evaluacionService.listarPorCurso(cursoId)
                        .stream()
                        .filter(e -> Boolean.TRUE.equals(e.getActivo()))
                        .toList());
    }

    // Buscar evaluación por ID
    @Operation(summary = "Consultar una evaluación autorizada")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionDTO> buscarPorId(
            @PathVariable Integer id,
            Principal principal) {

        EvaluacionDTO evaluacion =
                evaluacionService.buscarPorId(id);

        // Permitir acceso al propietario del curso
        if (esPropietarioCurso(
                evaluacion.getCursoId(), principal)) {

            return ResponseEntity.ok(evaluacion);
        }

        // Comprobar matrícula y estado de la evaluación
        if (!estaMatriculado(
                evaluacion.getCursoId(), principal)
                || !Boolean.TRUE.equals(evaluacion.getActivo())) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(evaluacion);
    }

    // Crear evaluación
    @Operation(summary = "Crear una evaluación en un curso propio")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<EvaluacionDTO> guardar(
            @RequestBody EvaluacionDTO dto,
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
                .body(evaluacionService.guardar(dto));
    }

    // Actualizar evaluación
    @Operation(summary = "Actualizar una evaluación propia")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody EvaluacionDTO dto,
            Principal principal) {

        EvaluacionDTO evaluacion =
                evaluacionService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietarioCurso(
                evaluacion.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                evaluacionService.actualizar(id, dto));
    }

    // Activar evaluación
    @Operation(summary = "Activar una evaluación")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id,
            Principal principal) {

        EvaluacionDTO evaluacion =
                evaluacionService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietarioCurso(
                evaluacion.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        evaluacionService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar evaluación
    @Operation(summary = "Desactivar una evaluación")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id,
            Principal principal) {

        EvaluacionDTO evaluacion =
                evaluacionService.buscarPorId(id);

        // Comprobar propietario del curso
        if (!esPropietarioCurso(
                evaluacion.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        evaluacionService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    // Comprobar propietario del curso
    private boolean esPropietarioCurso(
            Integer cursoId,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(cursoId);

        return usuarioService.buscarPorId(curso.getUsuarioId())
                .getCorreo()
                .equals(principal.getName());
    }

    // Comprobar si el estudiante está matriculado
    private boolean estaMatriculado(
            Integer cursoId,
            Principal principal) {

        CursoDTO curso = cursoService.buscarPorId(cursoId);

        // Comprobar que el curso esté activo
        if (!Boolean.TRUE.equals(curso.getActivo())) {
            return false;
        }

        return matriculaService.listarPorCurso(cursoId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                .anyMatch(m ->
                        usuarioService.buscarPorId(m.getEstudianteId())
                                .getCorreo()
                                .equals(principal.getName()));
    }
}