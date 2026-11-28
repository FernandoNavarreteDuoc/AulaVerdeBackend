package com.aulaverde.aulaverde.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDTO {

    private Integer id;

    private Integer puntajeObtenido;

    private Double nota;

    private String observacion;

    private LocalDate fechaCalificacion;

    @NotNull(message = "La evaluación es obligatoria")
    private Integer evaluacionId;

    @NotNull(message = "La matrícula es obligatoria")
    private Integer matriculaId;

    @Valid
    @NotEmpty(message = "Debe ingresar los detalles de calificación")
    private List<DetalleCalificacionDTO> detalles;

}