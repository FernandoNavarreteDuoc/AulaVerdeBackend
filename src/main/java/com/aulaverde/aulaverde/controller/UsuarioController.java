package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.UsuarioDTO;
import com.aulaverde.aulaverde.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios y perfiles")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Listar todos los usuarios
    @Operation(summary = "Listar todos los usuarios")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {

        return ResponseEntity.ok(usuarioService.listar());
    }

    // Listar usuarios activos
    @Operation(summary = "Listar usuarios activos")
    @PreAuthorize("denyAll()")
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioDTO>> listarActivos() {

        return ResponseEntity.ok(
                usuarioService.listarActivos());
    }

    // Buscar usuario por ID
    @Operation(summary = "Consultar perfil personal")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable Integer id,
            java.security.Principal principal) {

        UsuarioDTO usuario = usuarioService.buscarPorId(id);

        // Comprobar que sea el propietario del perfil
        if (!usuario.getCorreo().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(usuario);
    }

    // Listar usuarios por rol
    @Operation(summary = "Listar usuarios por rol")
    @PreAuthorize("denyAll()")
    @GetMapping("/rol/{nombreRol}")
    public ResponseEntity<List<UsuarioDTO>> listarPorRol(
            @PathVariable String nombreRol) {

        return ResponseEntity.ok(
                usuarioService.listarPorRol(nombreRol));
    }

    // Actualizar información personal
    @Operation(summary = "Actualizar perfil personal")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioDTO dto,
            java.security.Principal principal) {

        UsuarioDTO usuario = usuarioService.buscarPorId(id);

        // Comprobar que sea el propietario del perfil
        if (!usuario.getCorreo().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                usuarioService.actualizar(id, dto));
    }

    // Desactivar usuario
    @Operation(summary = "Desactivar usuario")
    @PreAuthorize("denyAll()")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id) {

        usuarioService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    // Activar usuario
    @Operation(summary = "Activar usuario")
    @PreAuthorize("denyAll()")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id) {

        usuarioService.activar(id);

        return ResponseEntity.noContent().build();
    }
}