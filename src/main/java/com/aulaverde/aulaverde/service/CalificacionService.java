package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.CalificacionDTO;
import com.aulaverde.aulaverde.DTO.DetalleCalificacionDTO;

import com.aulaverde.aulaverde.model.Calificacion;
import com.aulaverde.aulaverde.model.Evaluacion;
import com.aulaverde.aulaverde.model.Matricula;

import com.aulaverde.aulaverde.repository.CalificacionRepository;
import com.aulaverde.aulaverde.repository.EvaluacionRepository;
import com.aulaverde.aulaverde.repository.MatriculaRepository;
import com.aulaverde.aulaverde.repository.CriterioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private CriterioRepository criterioRepository;

    @Autowired
    private DetalleCalificacionService detalleService;

    // Listar todas las calificaciones
    public List<CalificacionDTO> listar() {

        List<CalificacionDTO> calificaciones = new ArrayList<>();

        for (Calificacion calificacion : calificacionRepository.findAll()) {

            calificaciones.add(convertirDTO(calificacion));
        }

        return calificaciones;
    }

    // Buscar calificación por ID
    public CalificacionDTO buscarPorId(Integer id) {

        return convertirDTO(obtenerCalificacion(id));
    }

    // Listar calificaciones de una evaluación
    public List<CalificacionDTO> listarPorEvaluacion(
            Integer evaluacionId) {

        List<CalificacionDTO> calificaciones = new ArrayList<>();

        for (Calificacion calificacion : calificacionRepository
                .findByEvaluacionId(evaluacionId)) {

            calificaciones.add(convertirDTO(calificacion));
        }

        return calificaciones;
    }

    // Listar calificaciones de una matrícula
    public List<CalificacionDTO> listarPorMatricula(
            Integer matriculaId) {

        List<CalificacionDTO> calificaciones = new ArrayList<>();

        for (Calificacion calificacion : calificacionRepository
                .findByMatriculaId(matriculaId)) {

            calificaciones.add(convertirDTO(calificacion));
        }

        return calificaciones;
    }

    // Registrar calificación completa
    @Transactional
    public CalificacionDTO guardar(CalificacionDTO dto) {

        Evaluacion evaluacion = evaluacionRepository
                .findById(dto.getEvaluacionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evaluación no encontrada"));

        Matricula matricula = matriculaRepository
                .findById(dto.getMatriculaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Matrícula no encontrada"));

        // Validar estados
        if (!Boolean.TRUE.equals(evaluacion.getActivo())
                || !Boolean.TRUE.equals(matricula.getActivo())
                || !Boolean.TRUE.equals(evaluacion.getCurso().getActivo())
                || !Boolean.TRUE.equals(matricula.getEstudiante().getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La evaluación, matrícula, curso o estudiante está desactivado");
        }

        // Validar que pertenezcan al mismo curso
        if (!evaluacion.getCurso().getId()
                .equals(matricula.getCurso().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estudiante no pertenece al curso");
        }

        // Evitar calificaciones duplicadas
        if (calificacionRepository
                .existsByEvaluacionIdAndMatriculaId(
                        evaluacion.getId(), matricula.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El estudiante ya tiene una calificación");
        }

        // Obtener cantidad de criterios de la rúbrica
        int cantidadCriterios = criterioRepository
                .findByRubricaIdOrderByOrdenAsc(
                        evaluacion.getRubrica().getId()).size();

        // Validar que todos los criterios sean evaluados
        if (dto.getDetalles() == null
                || cantidadCriterios == 0
                || dto.getDetalles().size() != cantidadCriterios) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe evaluar todos los criterios");
        }

        // Evitar criterios repetidos
        Set<Integer> criteriosEvaluados = new HashSet<>();

        for (DetalleCalificacionDTO detalle : dto.getDetalles()) {

            if (!criteriosEvaluados.add(detalle.getCriterioId())) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede evaluar un criterio dos veces");
            }
        }

        // Crear calificación
        Calificacion calificacion = new Calificacion();

        calificacion.setEvaluacion(evaluacion);
        calificacion.setMatricula(matricula);
        calificacion.setFechaCalificacion(LocalDate.now());
        calificacion.setObservacion(dto.getObservacion());
        calificacion.setPuntajeObtenido(0);
        calificacion.setNota(1.0);

        calificacion = calificacionRepository.save(calificacion);

        // Guardar detalles y sumar puntajes
        int puntajeTotal = 0;

        for (DetalleCalificacionDTO detalleDTO : dto.getDetalles()) {

            DetalleCalificacionDTO detalleGuardado =
                    detalleService.guardar(detalleDTO, calificacion);

            puntajeTotal += detalleGuardado.getPuntajeObtenido();
        }

        // Calcular nota final
        Double nota = calcularNota(
                puntajeTotal,
                evaluacion.getRubrica().getPuntajeTotal(),
                evaluacion.getExigencia()
        );

        calificacion.setPuntajeObtenido(puntajeTotal);
        calificacion.setNota(nota);

        return convertirDTO(
                calificacionRepository.save(calificacion));
    }

    // Calcular nota de 1.0 a 7.0
    private Double calcularNota(
            Integer puntajeObtenido,
            Integer puntajeMaximo,
            Integer exigencia) {

        if (puntajeMaximo == null || puntajeMaximo <= 0
                || puntajeObtenido < 0
                || puntajeObtenido > puntajeMaximo
                || exigencia == null
                || exigencia < 1 || exigencia > 100) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos para calcular la nota no son válidos");
        }

        double puntajeAprobacion =
                puntajeMaximo * (exigencia / 100.0);

        double nota;

        // Escala inferior a 4.0
        if (puntajeObtenido <= puntajeAprobacion) {

            nota = 1.0 + (3.0 * puntajeObtenido
                    / puntajeAprobacion);

        } else {

            // Escala superior a 4.0
            nota = 4.0 + (3.0
                    * (puntajeObtenido - puntajeAprobacion)
                    / (puntajeMaximo - puntajeAprobacion));
        }

        // Redondear a un decimal
        return Math.round(nota * 10.0) / 10.0;
    }

    // Obtener calificación
    private Calificacion obtenerCalificacion(Integer id) {

        return calificacionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Calificación no encontrada"));
    }

    // Convertir modelo a DTO
    private CalificacionDTO convertirDTO(
            Calificacion calificacion) {

        return new CalificacionDTO(
                calificacion.getId(),
                calificacion.getPuntajeObtenido(),
                calificacion.getNota(),
                calificacion.getObservacion(),
                calificacion.getFechaCalificacion(),
                calificacion.getEvaluacion().getId(),
                calificacion.getMatricula().getId(),
                detalleService.listarPorCalificacion(
                        calificacion.getId())
        );
    }
}