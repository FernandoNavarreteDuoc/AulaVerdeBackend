package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.MatriculaDTO;
import com.aulaverde.aulaverde.model.Matricula;
import com.aulaverde.aulaverde.model.Usuario;
import com.aulaverde.aulaverde.model.Curso;

import com.aulaverde.aulaverde.repository.MatriculaRepository;
import com.aulaverde.aulaverde.repository.UsuarioRepository;
import com.aulaverde.aulaverde.repository.CursoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class MatriculaService {

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // Listar todas las matrículas
    public List<MatriculaDTO> listar() {

        List<MatriculaDTO> matriculas = new ArrayList<>();

        for (Matricula matricula : matriculaRepository.findAll()) {

            matriculas.add(convertirDTO(matricula));
        }

        return matriculas;
    }

    // Listar matrículas activas de un curso
    public List<MatriculaDTO> listarPorCurso(Integer cursoId) {

        List<MatriculaDTO> matriculas = new ArrayList<>();

        for (Matricula matricula : matriculaRepository
                .findByCursoIdAndActivoTrue(cursoId)) {

            matriculas.add(convertirDTO(matricula));
        }

        return matriculas;
    }

    // Listar matrículas activas de un estudiante
    public List<MatriculaDTO> listarPorEstudiante(Integer estudianteId) {

        List<MatriculaDTO> matriculas = new ArrayList<>();

        for (Matricula matricula : matriculaRepository
                .findByEstudianteIdAndActivoTrue(estudianteId)) {

            matriculas.add(convertirDTO(matricula));
        }

        return matriculas;
    }

    // Buscar matrícula por ID
    public MatriculaDTO buscarPorId(Integer id) {

        return convertirDTO(obtenerMatricula(id));
    }

    // Matricular estudiante
    public MatriculaDTO guardar(MatriculaDTO dto) {

        Usuario estudiante = usuarioRepository
                .findById(dto.getEstudianteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Estudiante no encontrado"));

        Curso curso = cursoRepository
                .findById(dto.getCursoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curso no encontrado"));

        // Validar rol
        if (!"ESTUDIANTE".equals(estudiante.getRol().getNombre())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario debe ser estudiante");
        }

        // Validar estado del estudiante
        if (!Boolean.TRUE.equals(estudiante.getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estudiante está desactivado");
        }

        // Validar estado del curso
        if (!Boolean.TRUE.equals(curso.getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El curso está desactivado");
        }

        // Evitar matrículas duplicadas
        if (matriculaRepository.existsByEstudianteIdAndCursoId(
                estudiante.getId(), curso.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El estudiante ya tiene una matrícula en este curso");
        }

        // Crear matrícula
        Matricula matricula = new Matricula();

        matricula.setEstudiante(estudiante);
        matricula.setCurso(curso);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setActivo(true);

        return convertirDTO(matriculaRepository.save(matricula));
    }

    // Desactivar matrícula
    public void desactivar(Integer id) {

        Matricula matricula = obtenerMatricula(id);

        matricula.setActivo(false);

        matriculaRepository.save(matricula);
    }

    // Activar matrícula
    public void activar(Integer id) {

        Matricula matricula = obtenerMatricula(id);

        if (!Boolean.TRUE.equals(matricula.getCurso().getActivo())
                || !Boolean.TRUE.equals(
                        matricula.getEstudiante().getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El curso o estudiante está desactivado");
        }

        matricula.setActivo(true);

        matriculaRepository.save(matricula);
    }

    // Obtener matrícula desde la base de datos
    private Matricula obtenerMatricula(Integer id) {

        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Matrícula no encontrada"));
    }

    // Convertir modelo a DTO
    private MatriculaDTO convertirDTO(Matricula matricula) {

        return new MatriculaDTO(
                matricula.getId(),
                matricula.getFechaMatricula(),
                matricula.getActivo(),
                matricula.getEstudiante().getId(),
                matricula.getCurso().getId()
        );
    }
}