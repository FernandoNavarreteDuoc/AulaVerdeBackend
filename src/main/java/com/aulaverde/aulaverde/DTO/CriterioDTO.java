package com.aulaverde.aulaverde.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterioDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El puntaje máximo es obligatorio")
    @Min(1)
    private Integer puntajeMaximo;

    @NotNull(message = "El orden es obligatorio")
    @Min(1)
    private Integer orden;

    @Valid
    @NotEmpty(message = "Debe tener niveles de desempeño")
    private List<NivelDesempenoDTO> niveles;

}