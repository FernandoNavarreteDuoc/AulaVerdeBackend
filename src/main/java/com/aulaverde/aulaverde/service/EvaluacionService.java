package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.EvaluacionDTO;
import com.aulaverde.aulaverde.model.Evaluacion;
import com.aulaverde.aulaverde.model.Curso;
import com.aulaverde.aulaverde.model.Rubrica;

import com.aulaverde.aulaverde.repository.EvaluacionRepository;
import com.aulaverde.aulaverde.repository.CursoRepository;
import com.aulaverde.aulaverde.repository.RubricaRepository;
import com.aulaverde.aulaverde.repository.CalificacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private RubricaRepository rubricaRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    // Listar todas las evaluaciones
    public List<EvaluacionDTO> listar() {

        List<EvaluacionDTO> evaluaciones = new ArrayList<>();

        for (Evaluacion evaluacion : evaluacionRepository.findAll()) {

            evaluaciones.add(convertirDTO(evaluacion));
        }

        return evaluaciones;
    }

    // Listar evaluaciones activas de un curso
    public List<EvaluacionDTO> listarPorCurso(Integer cursoId) {

        List<EvaluacionDTO> evaluaciones = new ArrayList<>();

        for (Evaluacion evaluacion : evaluacionRepository
                .findByCursoIdAndActivoTrue(cursoId)) {

            evaluaciones.add(convertirDTO(evaluacion));
        }

        return evaluaciones;
    }

    // Buscar evaluación por ID
    public EvaluacionDTO buscarPorId(Integer id) {

        return convertirDTO(obtenerEvaluacion(id));
    }

    // Crear evaluación
    public EvaluacionDTO guardar(EvaluacionDTO dto) {

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curso no encontrado"));

        Rubrica rubrica = rubricaRepository.findById(dto.getRubricaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rúbrica no encontrada"));

        // Validar estado del curso
        if (!Boolean.TRUE.equals(curso.getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El curso está desactivado");
        }

        // Validar estado de la rúbrica
        if (!Boolean.TRUE.equals(rubrica.getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La rúbrica está desactivada");
        }

        // Validar que pertenezcan al mismo usuario
        if (!curso.getUsuario().getId()
                .equals(rubrica.getUsuario().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El curso y la rúbrica deben tener el mismo propietario");
        }

        validarExigencia(dto.getExigencia());

        // Crear evaluación
        Evaluacion evaluacion = new Evaluacion();

        evaluacion.setNombre(dto.getNombre());
        evaluacion.setDescripcion(dto.getDescripcion());
        evaluacion.setFechaEvaluacion(dto.getFechaEvaluacion());
        evaluacion.setExigencia(dto.getExigencia());
        evaluacion.setActivo(true);
        evaluacion.setCurso(curso);
        evaluacion.setRubrica(rubrica);

        return convertirDTO(evaluacionRepository.save(evaluacion));
    }

    // Actualizar evaluación
    public EvaluacionDTO actualizar(Integer id, EvaluacionDTO dto) {

        Evaluacion evaluacion = obtenerEvaluacion(id);

        // Validar exigencia
        validarExigencia(dto.getExigencia());

        // Proteger las calificaciones existentes
        if (calificacionRepository.existsByEvaluacionId(id)
                && !evaluacion.getExigencia().equals(dto.getExigencia())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede modificar la exigencia de una evaluación calificada");
        }

        evaluacion.setNombre(dto.getNombre());
        evaluacion.setDescripcion(dto.getDescripcion());
        evaluacion.setFechaEvaluacion(dto.getFechaEvaluacion());
        evaluacion.setExigencia(dto.getExigencia());

        return convertirDTO(evaluacionRepository.save(evaluacion));
    }

    // Desactivar evaluación
    public void desactivar(Integer id) {

        Evaluacion evaluacion = obtenerEvaluacion(id);

        evaluacion.setActivo(false);

        evaluacionRepository.save(evaluacion);
    }

    // Activar evaluación
    public void activar(Integer id) {

        Evaluacion evaluacion = obtenerEvaluacion(id);

        if (!Boolean.TRUE.equals(evaluacion.getCurso().getActivo())
                || !Boolean.TRUE.equals(
                        evaluacion.getRubrica().getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El curso o la rúbrica está desactivado");
        }

        evaluacion.setActivo(true);

        evaluacionRepository.save(evaluacion);
    }

    // Validar porcentaje de exigencia
    private void validarExigencia(Integer exigencia) {

        if (exigencia == null || exigencia < 1 || exigencia > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La exigencia debe estar entre 1 y 100");
        }
    }

    // Obtener evaluación desde la base de datos
    private Evaluacion obtenerEvaluacion(Integer id) {

        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evaluación no encontrada"));
    }

    // Convertir modelo a DTO
    private EvaluacionDTO convertirDTO(Evaluacion evaluacion) {

        return new EvaluacionDTO(
                evaluacion.getId(),
                evaluacion.getNombre(),
                evaluacion.getDescripcion(),
                evaluacion.getFechaEvaluacion(),
                evaluacion.getExigencia(),
                evaluacion.getActivo(),
                evaluacion.getCurso().getId(),
                evaluacion.getRubrica().getId()
        );
    }
}