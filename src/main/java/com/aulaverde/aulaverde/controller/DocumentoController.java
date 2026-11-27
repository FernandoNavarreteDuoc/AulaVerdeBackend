package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.DocumentoDTO;
import com.aulaverde.aulaverde.service.DocumentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    // Listar todos los documentos
    @GetMapping
    public ResponseEntity<List<DocumentoDTO>> listar() {

        return ResponseEntity.ok(
                documentoService.listar());
    }

    // Listar documentos de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DocumentoDTO>> listarPorUsuario(
            @PathVariable Integer usuarioId) {

        return ResponseEntity.ok(
                documentoService.listarPorUsuario(usuarioId));
    }

    // Buscar documento por ID
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                documentoService.buscarPorId(id));
    }

    // Subir documento
    @PostMapping(
        value = "/usuario/{usuarioId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DocumentoDTO> guardar(
            @PathVariable Integer usuarioId,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(
                value = "descripcion",
                required = false
            ) String descripcion) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(documentoService.guardar(
                        archivo,
                        descripcion,
                        usuarioId));
    }

    // Actualizar nombre y descripción
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody DocumentoDTO dto) {

        return ResponseEntity.ok(
                documentoService.actualizar(id, dto));
    }

    // Descargar documento
    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(
            @PathVariable Integer id) {

        DocumentoDTO documento =
                documentoService.buscarPorId(id);

        byte[] archivo =
                documentoService.descargar(id);

        ContentDisposition disposition =
                ContentDisposition.attachment()
                        .filename(
                            documento.getNombre(),
                            StandardCharsets.UTF_8
                        )
                        .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(archivo.length)
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    disposition.toString()
                )
                .body(archivo);
    }

    // Eliminar documento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id) {

        documentoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}