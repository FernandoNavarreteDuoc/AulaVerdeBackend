package com.aulaverde.aulaverde.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private LocalDate fechaCreacion;

    private Boolean activo;

    private Integer usuarioId;

}