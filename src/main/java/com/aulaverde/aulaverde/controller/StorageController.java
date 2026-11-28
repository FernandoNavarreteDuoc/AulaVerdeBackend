package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.service.StorageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/storage")
@Tag(name = "Almacenamiento", description = "Verificación del almacenamiento en la nube")
public class StorageController {

    @Autowired
    private StorageService storageService;

    // Verificar conexión con Object Storage
    @Operation(summary = "Verificar conexión con Object Storage")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @GetMapping("/estado")
    public ResponseEntity<String> verificarConexion() {

        storageService.verificarConexion();

        return ResponseEntity.ok(
                "Conexión con Object Storage exitosa"
        );
    }
}