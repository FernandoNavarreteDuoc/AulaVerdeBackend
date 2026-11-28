package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.UsuarioDTO;
import com.aulaverde.aulaverde.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios y perfiles")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Listar todos los usuarios (bloqueado)
    @Operation(summary = "Listar todos los usuarios")
    @PreAuthorize("denyAll()")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {

        return ResponseEntity.ok(usuarioService.listar());
    }

    // Listar usuarios activos (bloqueado)
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
            Principal principal) {

        UsuarioDTO usuario = usuarioService.buscarPorId(id);

        // Comprobar que sea el propietario del perfil
        if (!usuario.getCorreo().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(usuario);
    }

    // Listar estudiantes activos para matricularlos
    @Operation(summary = "Consultar estudiantes disponibles para matricular")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/rol/{nombreRol}")
    public ResponseEntity<List<UsuarioDTO>> listarPorRol(
            @PathVariable String nombreRol) {

        // Permitir solamente la consulta de estudiantes
        if (!"ESTUDIANTE".equalsIgnoreCase(nombreRol)) {
            return ResponseEntity.status(403).build();
        }

        // Obtener solamente estudiantes activos
        List<UsuarioDTO> estudiantes = usuarioService
                .listarPorRol("ESTUDIANTE")
                .stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .toList();

        return ResponseEntity.ok(estudiantes);
    }

    // Actualizar información personal
    @Operation(summary = "Actualizar perfil personal")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioDTO dto,
            Principal principal) {

        UsuarioDTO usuario = usuarioService.buscarPorId(id);

        // Comprobar que sea el propietario del perfil
        if (!usuario.getCorreo().equals(principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                usuarioService.actualizar(id, dto));
    }

    // Cambiar rol personal entre docente e instructor
    @Operation(
            summary = "Cambiar rol personal",
            description = "Permite cambiar el rol propio entre DOCENTE e INSTRUCTOR"
    )
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PatchMapping("/{id}/rol")
    public ResponseEntity<UsuarioDTO> cambiarRol(
            @PathVariable Integer id,
            @RequestParam String nombreRol,
            Principal principal) {

        return ResponseEntity.ok(
                usuarioService.cambiarRol(
                        id,
                        nombreRol,
                        principal.getName()));
    }

    // Desactivar usuario (bloqueado)
    @Operation(summary = "Desactivar usuario")
    @PreAuthorize("denyAll()")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id) {

        usuarioService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    // Activar usuario (bloqueado)
    @Operation(summary = "Activar usuario")
    @PreAuthorize("denyAll()")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id) {

        usuarioService.activar(id);

        return ResponseEntity.noContent().build();
    }
}