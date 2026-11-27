package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.DetalleCalificacionDTO;
import com.aulaverde.aulaverde.model.DetalleCalificacion;
import com.aulaverde.aulaverde.model.Calificacion;
import com.aulaverde.aulaverde.model.Criterio;
import com.aulaverde.aulaverde.model.NivelDesempeno;

import com.aulaverde.aulaverde.repository.DetalleCalificacionRepository;
import com.aulaverde.aulaverde.repository.CriterioRepository;
import com.aulaverde.aulaverde.repository.NivelDesempenoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@Service
public class DetalleCalificacionService {

    @Autowired
    private DetalleCalificacionRepository detalleRepository;

    @Autowired
    private CriterioRepository criterioRepository;

    @Autowired
    private NivelDesempenoRepository nivelRepository;

    // Listar detalles de una calificación
    public List<DetalleCalificacionDTO> listarPorCalificacion(
            Integer calificacionId) {

        List<DetalleCalificacionDTO> detalles = new ArrayList<>();

        for (DetalleCalificacion detalle : detalleRepository
                .findByCalificacionId(calificacionId)) {

            detalles.add(convertirDTO(detalle));
        }

        return detalles;
    }

    // Guardar detalle de calificación
    public DetalleCalificacionDTO guardar(
            DetalleCalificacionDTO dto,
            Calificacion calificacion) {

        Criterio criterio = criterioRepository
                .findById(dto.getCriterioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Criterio no encontrado"));

        NivelDesempeno nivel = nivelRepository
                .findById(dto.getNivelDesempenoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nivel de desempeño no encontrado"));

        // Validar que el criterio pertenezca a la rúbrica
        if (!criterio.getRubrica().getId().equals(
                calificacion.getEvaluacion().getRubrica().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El criterio no pertenece a la rúbrica");
        }

        // Validar que el nivel pertenezca al criterio
        if (!nivel.getCriterio().getId().equals(criterio.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nivel no pertenece al criterio");
        }

        // Crear detalle
        DetalleCalificacion detalle = new DetalleCalificacion();

        detalle.setCalificacion(calificacion);
        detalle.setCriterio(criterio);
        detalle.setNivelDesempeno(nivel);
        detalle.setPuntajeObtenido(nivel.getPuntaje());
        detalle.setObservacion(dto.getObservacion());

        return convertirDTO(detalleRepository.save(detalle));
    }

    // Convertir modelo a DTO
    private DetalleCalificacionDTO convertirDTO(
            DetalleCalificacion detalle) {

        return new DetalleCalificacionDTO(
                detalle.getId(),
                detalle.getPuntajeObtenido(),
                detalle.getObservacion(),
                detalle.getCriterio().getId(),
                detalle.getNivelDesempeno().getId()
        );
    }
}