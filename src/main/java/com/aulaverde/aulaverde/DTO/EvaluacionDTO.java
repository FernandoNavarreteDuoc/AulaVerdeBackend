package com.aulaverde.aulaverde.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaEvaluacion;

    @NotNull(message = "La exigencia es obligatoria")
    @Min(value = 1, message = "La exigencia mínima es 1%")
    @Max(value = 100, message = "La exigencia máxima es 100%")
    private Integer exigencia = 60;

    private Boolean activo;

    @NotNull(message = "El curso es obligatorio")
    private Integer cursoId;

    @NotNull(message = "La rúbrica es obligatoria")
    private Integer rubricaId;

}