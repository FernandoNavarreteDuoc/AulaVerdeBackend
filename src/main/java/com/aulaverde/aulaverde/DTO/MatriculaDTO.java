package com.aulaverde.aulaverde.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDTO {

    private Integer id;

    private LocalDate fechaMatricula;

    private Boolean activo;

    @NotNull(message = "El estudiante es obligatorio")
    private Integer estudianteId;

    @NotNull(message = "El curso es obligatorio")
    private Integer cursoId;

}