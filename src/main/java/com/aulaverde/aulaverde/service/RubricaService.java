package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.RubricaDTO;
import com.aulaverde.aulaverde.DTO.CriterioDTO;

import com.aulaverde.aulaverde.model.Rubrica;
import com.aulaverde.aulaverde.model.Usuario;

import com.aulaverde.aulaverde.repository.RubricaRepository;
import com.aulaverde.aulaverde.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class RubricaService {

    @Autowired
    private RubricaRepository rubricaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CriterioService criterioService;

    // Listar todas las rúbricas
    public List<RubricaDTO> listar() {

        List<RubricaDTO> rubricas = new ArrayList<>();

        for (Rubrica rubrica : rubricaRepository.findAll()) {

            rubricas.add(convertirDTO(rubrica));
        }

        return rubricas;
    }

    // Listar rúbricas activas de un usuario
    public List<RubricaDTO> listarPorUsuario(Integer usuarioId) {

        List<RubricaDTO> rubricas = new ArrayList<>();

        for (Rubrica rubrica : rubricaRepository
                .findByUsuarioIdAndActivoTrue(usuarioId)) {

            rubricas.add(convertirDTO(rubrica));
        }

        return rubricas;
    }

    // Buscar rúbrica por ID
    public RubricaDTO buscarPorId(Integer id) {

        return convertirDTO(obtenerRubrica(id));
    }

    // Crear rúbrica completa
    @Transactional
    public RubricaDTO guardar(RubricaDTO dto, Integer usuarioId) {

        // Buscar usuario propietario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        // Comprobar estado del usuario
        if (!Boolean.TRUE.equals(usuario.getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario está desactivado");
        }

        // Validar rol del usuario
        String rol = usuario.getRol().getNombre();

        if (!"DOCENTE".equals(rol) && !"INSTRUCTOR".equals(rol)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no puede crear rúbricas");
        }

        // Validar criterios
        if (dto.getCriterios() == null
                || dto.getCriterios().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La rúbrica debe tener criterios");
        }

        // Calcular puntaje total
        Integer puntajeTotal = 0;

        for (CriterioDTO criterio : dto.getCriterios()) {

            if (criterio.getPuntajeMaximo() == null
                    || criterio.getPuntajeMaximo() <= 0) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El puntaje del criterio no es válido");
            }

            puntajeTotal += criterio.getPuntajeMaximo();
        }

        // Crear rúbrica
        Rubrica rubrica = new Rubrica();

        rubrica.setNombre(dto.getNombre());
        rubrica.setDescripcion(dto.getDescripcion());
        rubrica.setPuntajeTotal(puntajeTotal);
        rubrica.setFechaCreacion(LocalDate.now());
        rubrica.setActivo(true);
        rubrica.setUsuario(usuario);

        rubrica = rubricaRepository.save(rubrica);

        // Guardar criterios y niveles
        for (CriterioDTO criterioDTO : dto.getCriterios()) {

            criterioService.guardar(criterioDTO, rubrica);
        }

        return convertirDTO(rubrica);
    }

    // Desactivar rúbrica
    public void desactivar(Integer id) {

        Rubrica rubrica = obtenerRubrica(id);

        rubrica.setActivo(false);

        rubricaRepository.save(rubrica);
    }

    // Activar rúbrica
    public void activar(Integer id) {

        Rubrica rubrica = obtenerRubrica(id);

        rubrica.setActivo(true);

        rubricaRepository.save(rubrica);
    }

    // Obtener rúbrica desde la base de datos
    private Rubrica obtenerRubrica(Integer id) {

        return rubricaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rúbrica no encontrada"));
    }

    // Convertir modelo a DTO
    private RubricaDTO convertirDTO(Rubrica rubrica) {

        return new RubricaDTO(
                rubrica.getId(),
                rubrica.getNombre(),
                rubrica.getDescripcion(),
                rubrica.getPuntajeTotal(),
                rubrica.getFechaCreacion(),
                rubrica.getActivo(),
                rubrica.getUsuario().getId(),
                criterioService.listarPorRubrica(rubrica.getId())
        );
    }
}