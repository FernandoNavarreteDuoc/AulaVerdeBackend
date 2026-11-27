package com.aulaverde.aulaverde.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NivelDesempenoDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El puntaje es obligatorio")
    @Min(value = 0, message = "El puntaje no puede ser negativo")
    private Integer puntaje;

    @NotNull(message = "El orden es obligatorio")
    @Min(1)
    private Integer orden;

}