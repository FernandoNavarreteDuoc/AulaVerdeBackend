package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.CriterioDTO;
import com.aulaverde.aulaverde.DTO.NivelDesempenoDTO;

import com.aulaverde.aulaverde.model.Criterio;
import com.aulaverde.aulaverde.model.Rubrica;

import com.aulaverde.aulaverde.repository.CriterioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@Service
public class CriterioService {

    @Autowired
    private CriterioRepository criterioRepository;

    @Autowired
    private NivelDesempenoService nivelDesempenoService;

    // Listar criterios de una rúbrica
    public List<CriterioDTO> listarPorRubrica(Integer rubricaId) {

        List<CriterioDTO> criterios = new ArrayList<>();

        for (Criterio criterio : criterioRepository
                .findByRubricaIdOrderByOrdenAsc(rubricaId)) {

            criterios.add(convertirDTO(criterio));
        }

        return criterios;
    }

    // Guardar criterio y sus niveles
    @Transactional
    public CriterioDTO guardar(CriterioDTO dto, Rubrica rubrica) {

        if (dto.getPuntajeMaximo() == null
                || dto.getPuntajeMaximo() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El puntaje máximo debe ser mayor a cero");
        }

        if (dto.getNiveles() == null || dto.getNiveles().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El criterio debe tener niveles de desempeño");
        }

        Criterio criterio = new Criterio();

        criterio.setNombre(dto.getNombre());
        criterio.setDescripcion(dto.getDescripcion());
        criterio.setPuntajeMaximo(dto.getPuntajeMaximo());
        criterio.setOrden(dto.getOrden());
        criterio.setRubrica(rubrica);

        criterio = criterioRepository.save(criterio);

        // Guardar los niveles del criterio
        for (NivelDesempenoDTO nivelDTO : dto.getNiveles()) {

            nivelDesempenoService.guardar(nivelDTO, criterio);
        }

        return convertirDTO(criterio);
    }

    // Convertir modelo a DTO
    private CriterioDTO convertirDTO(Criterio criterio) {

        return new CriterioDTO(
                criterio.getId(),
                criterio.getNombre(),
                criterio.getDescripcion(),
                criterio.getPuntajeMaximo(),
                criterio.getOrden(),
                nivelDesempenoService.listarPorCriterio(
                        criterio.getId())
        );
    }
}