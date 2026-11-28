package com.aulaverde.aulaverde.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCalificacionDTO {

    private Integer id;

    private Integer puntajeObtenido;

    private String observacion;

    @NotNull(message = "El criterio es obligatorio")
    private Integer criterioId;

    @NotNull(message = "El nivel de desempeño es obligatorio")
    private Integer nivelDesempenoId;

}