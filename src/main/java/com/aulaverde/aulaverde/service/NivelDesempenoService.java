package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.NivelDesempenoDTO;
import com.aulaverde.aulaverde.model.Criterio;
import com.aulaverde.aulaverde.model.NivelDesempeno;
import com.aulaverde.aulaverde.repository.NivelDesempenoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@Service
public class NivelDesempenoService {

    @Autowired
    private NivelDesempenoRepository nivelRepository;

    // Listar niveles de un criterio
    public List<NivelDesempenoDTO> listarPorCriterio(Integer criterioId) {

        List<NivelDesempenoDTO> niveles = new ArrayList<>();

        for (NivelDesempeno nivel : nivelRepository
                .findByCriterioIdOrderByOrdenAsc(criterioId)) {

            niveles.add(convertirDTO(nivel));
        }

        return niveles;
    }

    // Guardar nivel de desempeño
    public NivelDesempenoDTO guardar(
            NivelDesempenoDTO dto, Criterio criterio) {

        // Validar puntaje
        if (dto.getPuntaje() == null
                || dto.getPuntaje() < 0
                || dto.getPuntaje() > criterio.getPuntajeMaximo()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El puntaje del nivel no es válido");
        }

        NivelDesempeno nivel = new NivelDesempeno();

        nivel.setNombre(dto.getNombre());
        nivel.setDescripcion(dto.getDescripcion());
        nivel.setPuntaje(dto.getPuntaje());
        nivel.setOrden(dto.getOrden());
        nivel.setCriterio(criterio);

        return convertirDTO(nivelRepository.save(nivel));
    }

    // Convertir modelo a DTO
    private NivelDesempenoDTO convertirDTO(NivelDesempeno nivel) {

        return new NivelDesempenoDTO(
                nivel.getId(),
                nivel.getNombre(),
                nivel.getDescripcion(),
                nivel.getPuntaje(),
                nivel.getOrden()
        );
    }
}