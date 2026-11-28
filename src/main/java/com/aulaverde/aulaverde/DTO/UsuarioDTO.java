package com.aulaverde.aulaverde.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private Boolean activo;
    private RolDTO rol;

}