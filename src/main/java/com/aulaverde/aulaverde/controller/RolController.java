package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.RolDTO;
import com.aulaverde.aulaverde.service.RolService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Consulta de roles del sistema")
public class RolController {

    @Autowired
    private RolService rolService;

    // Listar todos los roles
    @Operation(summary = "Listar todos los roles")
    @GetMapping
    public ResponseEntity<List<RolDTO>> listar() {

        return ResponseEntity.ok(rolService.listar());
    }

    // Buscar rol por ID
    @Operation(summary = "Buscar un rol por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                rolService.buscarPorId(id));
    }
}