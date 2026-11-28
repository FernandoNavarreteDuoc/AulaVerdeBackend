package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.RubricaDTO;
import com.aulaverde.aulaverde.DTO.CursoDTO;
import com.aulaverde.aulaverde.DTO.EvaluacionDTO;
import com.aulaverde.aulaverde.DTO.MatriculaDTO;

import com.aulaverde.aulaverde.service.RubricaService;
import com.aulaverde.aulaverde.service.UsuarioService;
import com.aulaverde.aulaverde.service.MatriculaService;
import com.aulaverde.aulaverde.service.EvaluacionService;
import com.aulaverde.aulaverde.service.CursoService;

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
@RequestMapping("/api/rubricas")
@Tag(name = "Rúbricas", description = "Gestión de rúbricas, criterios y niveles de desempeño")
public class RubricaController {

    @Autowired
    private RubricaService rubricaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private CursoService cursoService;

    // Listar todas las rúbricas (bloqueado)
    @Operation(summary = "Listar todas las rúbricas")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<RubricaDTO>> listar() {

        return ResponseEntity.ok(rubricaService.listar());
    }

    // Listar rúbricas de un usuario
    @Operation(summary = "Listar rúbricas propias")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RubricaDTO>> listarPorUsuario(
            @PathVariable Integer usuarioId,
            Principal principal) {

        // Comprobar propietario
        if (!esPropietario(usuarioId, principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                rubricaService.listarPorUsuario(usuarioId));
    }

    // Buscar rúbrica por ID
    @Operation(summary = "Consultar una rúbrica propia")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<RubricaDTO> buscarPorId(
            @PathVariable Integer id,
            Principal principal) {

        RubricaDTO rubrica = rubricaService.buscarPorId(id);

        // Comprobar propietario
        if (!esPropietario(rubrica.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(rubrica);
    }

    // Consultar rúbrica asociada a una evaluación
    @Operation(summary = "Consultar la rúbrica de una evaluación autorizada")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<RubricaDTO> buscarPorEvaluacion(
            @PathVariable Integer evaluacionId,
            Principal principal) {

        EvaluacionDTO evaluacion =
                evaluacionService.buscarPorId(evaluacionId);

        CursoDTO curso =
                cursoService.buscarPorId(evaluacion.getCursoId());

        // Permitir acceso al propietario del curso
        if (esPropietario(curso.getUsuarioId(), principal)) {

            return ResponseEntity.ok(
                    rubricaService.buscarPorId(
                            evaluacion.getRubricaId()));
        }

        // Comprobar que el curso y la evaluación estén activos
        if (!Boolean.TRUE.equals(curso.getActivo())
                || !Boolean.TRUE.equals(evaluacion.getActivo())) {

            return ResponseEntity.status(403).build();
        }

        // Comprobar matrícula activa del estudiante
        if (!estaMatriculado(curso.getId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                rubricaService.buscarPorId(
                        evaluacion.getRubricaId()));
    }

    // Crear rúbrica
    @Operation(summary = "Crear una rúbrica")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<RubricaDTO> guardar(
            @PathVariable Integer usuarioId,
            @RequestBody RubricaDTO dto,
            Principal principal) {

        // Comprobar que el usuario cree rúbricas a su nombre
        if (!esPropietario(usuarioId, principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rubricaService.guardar(dto, usuarioId));
    }

    // Activar rúbrica
    @Operation(summary = "Activar una rúbrica")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id,
            Principal principal) {

        RubricaDTO rubrica = rubricaService.buscarPorId(id);

        // Comprobar propietario
        if (!esPropietario(rubrica.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        rubricaService.activar(id);

        return ResponseEntity.noContent().build();
    }

    // Desactivar rúbrica
    @Operation(summary = "Desactivar una rúbrica")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id,
            Principal principal) {

        RubricaDTO rubrica = rubricaService.buscarPorId(id);

        // Comprobar propietario
        if (!esPropietario(rubrica.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        rubricaService.desactivar(id);

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

    // Comprobar matrícula activa del estudiante
    private boolean estaMatriculado(
            Integer cursoId,
            Principal principal) {

        List<MatriculaDTO> matriculas =
                matriculaService.listarPorCurso(cursoId);

        return matriculas.stream()
                .filter(m -> Boolean.TRUE.equals(m.getActivo()))
                .anyMatch(m ->
                        esPropietario(
                                m.getEstudianteId(), principal));
    }
}