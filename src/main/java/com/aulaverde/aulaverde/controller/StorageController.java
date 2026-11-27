package com.aulaverde.aulaverde.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import com.aulaverde.aulaverde.service.StorageService;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/estado")
    public ResponseEntity<String> verificarConexion() {

        storageService.verificarConexion();

        return ResponseEntity.ok(
            "Conexion con Object Storage exitosa"
        );
    }
}