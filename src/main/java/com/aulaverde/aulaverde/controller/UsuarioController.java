package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.UsuarioDTO;
import com.aulaverde.aulaverde.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {

        return ResponseEntity.ok(usuarioService.listar());
    }

    // Listar usuarios activos
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioDTO>> listarActivos() {

        return ResponseEntity.ok(
                usuarioService.listarActivos());
    }

    // Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id));
    }

    // Listar usuarios por rol
    @GetMapping("/rol/{nombreRol}")
    public ResponseEntity<List<UsuarioDTO>> listarPorRol(
            @PathVariable String nombreRol) {

        return ResponseEntity.ok(
                usuarioService.listarPorRol(nombreRol));
    }

    // Actualizar información personal
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody UsuarioDTO dto) {

        return ResponseEntity.ok(
                usuarioService.actualizar(id, dto));
    }

    // Desactivar usuario
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(
            @PathVariable Integer id) {

        usuarioService.desactivar(id);

        return ResponseEntity.noContent().build();
    }

    // Activar usuario
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(
            @PathVariable Integer id) {

        usuarioService.activar(id);

        return ResponseEntity.noContent().build();
    }
}