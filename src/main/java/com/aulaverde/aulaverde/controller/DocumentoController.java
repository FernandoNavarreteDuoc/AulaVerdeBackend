package com.aulaverde.aulaverde.controller;

import com.aulaverde.aulaverde.DTO.DocumentoDTO;
import com.aulaverde.aulaverde.service.DocumentoService;
import com.aulaverde.aulaverde.service.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@Tag(name = "Documentos", description = "Gestión de documentos académicos")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private UsuarioService usuarioService;

    // Listar todos los documentos
    @Operation(summary = "Listar todos los documentos")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping
    public ResponseEntity<List<DocumentoDTO>> listar() {

        return ResponseEntity.ok(
                documentoService.listar());
    }

    // Listar documentos de un usuario
    @Operation(summary = "Listar documentos por usuario")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DocumentoDTO>> listarPorUsuario(
            @PathVariable Integer usuarioId) {

        return ResponseEntity.ok(
                documentoService.listarPorUsuario(usuarioId));
    }

    // Buscar documento por ID
    @Operation(summary = "Buscar documento por ID")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoDTO> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                documentoService.buscarPorId(id));
    }

    // Subir documento
    @Operation(summary = "Subir un documento")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
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
            ) String descripcion,
            Principal principal) {

        // Comprobar que el usuario suba documentos a su nombre
        if (!esPropietario(usuarioId, principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(documentoService.guardar(
                        archivo,
                        descripcion,
                        usuarioId));
    }

    // Actualizar nombre y descripción
    @Operation(summary = "Actualizar información de un documento")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody DocumentoDTO dto,
            Principal principal) {

        DocumentoDTO documento =
                documentoService.buscarPorId(id);

        // Comprobar propietario del documento
        if (!esPropietario(documento.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                documentoService.actualizar(id, dto));
    }

    // Descargar documento
    @Operation(summary = "Descargar un documento")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR', 'ESTUDIANTE')")
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
    @Operation(summary = "Eliminar un documento")
    @PreAuthorize("hasAnyRole('DOCENTE', 'INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id,
            Principal principal) {

        DocumentoDTO documento =
                documentoService.buscarPorId(id);

        // Comprobar propietario del documento
        if (!esPropietario(documento.getUsuarioId(), principal)) {
            return ResponseEntity.status(403).build();
        }

        documentoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // Comprobar si el usuario autenticado es el propietario
    private boolean esPropietario(
            Integer usuarioId,
            Principal principal) {

        return usuarioService.buscarPorId(usuarioId)
                .getCorreo()
                .equals(principal.getName());
    }
}