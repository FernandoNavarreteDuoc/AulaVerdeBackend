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
public class RubricaDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private Integer puntajeTotal;

    private LocalDate fechaCreacion;

    private Boolean activo;

    private Integer usuarioId;

    @Valid
    @NotEmpty(message = "La rúbrica debe tener criterios")
    private List<CriterioDTO> criterios;

}