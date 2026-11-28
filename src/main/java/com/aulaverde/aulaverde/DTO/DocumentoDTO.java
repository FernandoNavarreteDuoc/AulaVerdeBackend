package com.aulaverde.aulaverde.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoDTO {

    private Integer id;

    private String nombre;

    private String descripcion;

    private String tipoArchivo;

    private Long tamano;

    private LocalDate fechaSubida;

    private Integer usuarioId;

}