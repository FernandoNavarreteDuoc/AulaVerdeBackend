package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.LoginDTO;
import com.aulaverde.aulaverde.DTO.RegistroDTO;
import com.aulaverde.aulaverde.DTO.UsuarioDTO;
import com.aulaverde.aulaverde.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Registrar usuario
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(
            @Valid @RequestBody RegistroDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registrar(dto));
    }

    // Iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(
            @Valid @RequestBody LoginDTO dto) {

        return ResponseEntity.ok(
                authService.login(dto));
    }
}