package com.aulaverde.aulaverde.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;

    private String tipoToken;

    private UsuarioDTO usuario;
}