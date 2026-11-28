package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.CalificacionDTO;
import com.aulaverde.aulaverde.DTO.EvaluacionDTO;
import com.aulaverde.aulaverde.DTO.MatriculaDTO;
import com.aulaverde.aulaverde.DTO.CursoDTO;

import com.aulaverde.aulaverde.service.CalificacionService;
import com.aulaverde.aulaverde.service.EvaluacionService;
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
@RequestMapping("/api/calificaciones")
@Tag(name = "Calificaciones", description = "Registro y consulta de calificaciones académicas")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private UsuarioService usuarioService;

    // Listar todas las calificaciones (bloqueado)
    @Operation(summary = "Listar todas las calificaciones")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<CalificacionDTO>> listar() {

        return ResponseEntity.ok(
                calificacionService.listar());
    }

    // Buscar calificación por ID
    @Operation(summary = "Consultar una calificación autorizada")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionDTO> buscarPorId(
            @PathVariable Integer id,
            Principal principal) {

        CalificacionDTO calificacion =
                calificacionService.buscarPorId(id);

        MatriculaDTO matricula =
                matriculaService.buscarPorId(
                        calificacion.getMatriculaId());

        // Comprobar estudiante o propietario del curso
        if (!esUsuario(matricula.getEstudianteId(), principal)
                && !esPropietarioCurso(
                        matricula.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(calificacion);
    }

    // Listar calificaciones de una evaluación
    @Operation(summary = "Listar calificaciones de una evaluación propia")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<List<CalificacionDTO>> listarPorEvaluacion(
            @PathVariable Integer evaluacionId,
            Principal principal) {

        EvaluacionDTO evaluacion =
                evaluacionService.buscarPorId(evaluacionId);

        // Comprobar propietario del curso
        if (!esPropietarioCurso(
                evaluacion.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                calificacionService.listarPorEvaluacion(evaluacionId));
    }

    // Listar calificaciones de una matrícula
    @Operation(summary = "Consultar calificaciones de una matrícula autorizada")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/matricula/{matriculaId}")
    public ResponseEntity<List<CalificacionDTO>> listarPorMatricula(
            @PathVariable Integer matriculaId,
            Principal principal) {

        MatriculaDTO matricula =
                matriculaService.buscarPorId(matriculaId);

        // Comprobar estudiante o propietario del curso
        if (!esUsuario(matricula.getEstudianteId(), principal)
                && !esPropietarioCurso(
                        matricula.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                calificacionService.listarPorMatricula(matriculaId));
    }

    // Registrar calificación
    @Operation(summary = "Registrar una calificación en un curso propio")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<CalificacionDTO> guardar(
            @RequestBody CalificacionDTO dto,
            Principal principal) {

        // Comprobar datos obligatorios
        if (dto.getEvaluacionId() == null
                || dto.getMatriculaId() == null) {

            return ResponseEntity.badRequest().build();
        }

        EvaluacionDTO evaluacion =
                evaluacionService.buscarPorId(
                        dto.getEvaluacionId());

        MatriculaDTO matricula =
                matriculaService.buscarPorId(
                        dto.getMatriculaId());

        // Comprobar propietario del curso
        if (!esPropietarioCurso(
                evaluacion.getCursoId(), principal)) {

            return ResponseEntity.status(403).build();
        }

        // Comprobar que la matrícula pertenezca al mismo curso
        if (!evaluacion.getCursoId().equals(
                matricula.getCursoId())) {

            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(calificacionService.guardar(dto));
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