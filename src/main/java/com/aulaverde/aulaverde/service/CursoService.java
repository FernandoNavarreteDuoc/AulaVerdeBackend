package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.CursoDTO;
import com.aulaverde.aulaverde.model.Curso;
import com.aulaverde.aulaverde.model.Usuario;
import com.aulaverde.aulaverde.repository.CursoRepository;
import com.aulaverde.aulaverde.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todos los cursos
    public List<CursoDTO> listar() {

        List<CursoDTO> cursos = new ArrayList<>();

        for (Curso curso : cursoRepository.findAll()) {
            cursos.add(convertirDTO(curso));
        }

        return cursos;
    }

    // Listar cursos activos de un usuario
    public List<CursoDTO> listarPorUsuario(Integer usuarioId) {

        List<CursoDTO> cursos = new ArrayList<>();

        for (Curso curso : cursoRepository
                .findByUsuarioIdAndActivoTrue(usuarioId)) {

            cursos.add(convertirDTO(curso));
        }

        return cursos;
    }

    // Buscar curso por ID
    public CursoDTO buscarPorId(Integer id) {

        return convertirDTO(obtenerCurso(id));
    }

    // Crear curso
    public CursoDTO guardar(CursoDTO dto, Integer usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        // Validar estado del usuario
        if (!Boolean.TRUE.equals(usuario.getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario está desactivado");
        }

        // Validar rol
        String rol = usuario.getRol().getNombre();

        if (!"DOCENTE".equals(rol) && !"INSTRUCTOR".equals(rol)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no puede crear cursos");
        }

        Curso curso = new Curso();

        curso.setNombre(dto.getNombre());
        curso.setDescripcion(dto.getDescripcion());
        curso.setFechaCreacion(LocalDate.now());
        curso.setActivo(true);
        curso.setUsuario(usuario);

        return convertirDTO(cursoRepository.save(curso));
    }

    // Actualizar curso
    public CursoDTO actualizar(Integer id, CursoDTO dto) {

        Curso curso = obtenerCurso(id);

        curso.setNombre(dto.getNombre());
        curso.setDescripcion(dto.getDescripcion());

        return convertirDTO(cursoRepository.save(curso));
    }

    // Desactivar curso
    public void desactivar(Integer id) {

        Curso curso = obtenerCurso(id);

        curso.setActivo(false);

        cursoRepository.save(curso);
    }

    // Activar curso
    public void activar(Integer id) {

        Curso curso = obtenerCurso(id);

        curso.setActivo(true);

        cursoRepository.save(curso);
    }

    // Obtener curso desde la base de datos
    private Curso obtenerCurso(Integer id) {

        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curso no encontrado"));
    }

    // Convertir modelo a DTO
    private CursoDTO convertirDTO(Curso curso) {

        return new CursoDTO(
                curso.getId(),
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getFechaCreacion(),
                curso.getActivo(),
                curso.getUsuario().getId()
        );
    }
}