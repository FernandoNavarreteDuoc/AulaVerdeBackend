package com.aulaverde.aulaverde.service;

import com.aulaverde.aulaverde.DTO.DocumentoDTO;

import com.aulaverde.aulaverde.model.Documento;
import com.aulaverde.aulaverde.model.Usuario;

import com.aulaverde.aulaverde.repository.DocumentoRepository;
import com.aulaverde.aulaverde.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private StorageService storageService;

    // Listar todos los documentos
    public List<DocumentoDTO> listar() {

        List<DocumentoDTO> documentos = new ArrayList<>();

        for (Documento documento : documentoRepository.findAll()) {

            documentos.add(convertirDTO(documento));
        }

        return documentos;
    }

    // Listar documentos de un usuario
    public List<DocumentoDTO> listarPorUsuario(Integer usuarioId) {

        List<DocumentoDTO> documentos = new ArrayList<>();

        for (Documento documento : documentoRepository
                .findByUsuarioId(usuarioId)) {

            documentos.add(convertirDTO(documento));
        }

        return documentos;
    }

    // Buscar documento por ID
    public DocumentoDTO buscarPorId(Integer id) {

        return convertirDTO(obtenerDocumento(id));
    }

    // Subir y guardar documento
    public DocumentoDTO guardar(
            MultipartFile archivo,
            String descripcion,
            Integer usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));

        // Validar estado del usuario
        if (!Boolean.TRUE.equals(usuario.getActivo())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario está desactivado");
        }

        // Validar archivo
        if (archivo == null || archivo.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo está vacío");
        }

        // Validar nombre
        String nombre = archivo.getOriginalFilename();

        if (nombre == null || nombre.isBlank()
                || nombre.length() > 255) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del archivo no es válido");
        }

        // Validar descripción
        if (descripcion != null && descripcion.length() > 1000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción supera los 1000 caracteres");
        }

        // Obtener tipo de archivo
        String tipoArchivo = archivo.getContentType();

        if (tipoArchivo == null || tipoArchivo.isBlank()) {

            tipoArchivo = "application/octet-stream";
        }

        // Subir archivo a Object Storage
        String claveArchivo;

        try {

            claveArchivo = storageService.subirArchivo(archivo);

        } catch (IOException | RuntimeException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo subir el archivo");
        }

        // Crear registro del documento
        Documento documento = new Documento();

        documento.setNombre(nombre);
        documento.setDescripcion(descripcion);
        documento.setTipoArchivo(tipoArchivo);
        documento.setClaveArchivo(claveArchivo);
        documento.setTamano(archivo.getSize());
        documento.setFechaSubida(LocalDate.now());
        documento.setUsuario(usuario);

        // Guardar información en PostgreSQL
        try {

            return convertirDTO(
                    documentoRepository.saveAndFlush(documento));

        } catch (RuntimeException e) {

            // Intentar eliminar el archivo si falla la base de datos
            try {

                storageService.eliminarArchivo(claveArchivo);

            } catch (RuntimeException errorStorage) {

                e.addSuppressed(errorStorage);
            }

            throw e;
        }
    }

    // Actualizar información del documento
    public DocumentoDTO actualizar(
            Integer id, DocumentoDTO dto) {

        Documento documento = obtenerDocumento(id);

        // Validar nombre
        if (dto.getNombre() == null
                || dto.getNombre().isBlank()
                || dto.getNombre().length() > 255) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no es válido");
        }

        // Validar descripción
        if (dto.getDescripcion() != null
                && dto.getDescripcion().length() > 1000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción supera los 1000 caracteres");
        }

        documento.setNombre(dto.getNombre());
        documento.setDescripcion(dto.getDescripcion());

        return convertirDTO(
                documentoRepository.save(documento));
    }

    // Descargar documento
    public byte[] descargar(Integer id) {

        Documento documento = obtenerDocumento(id);

        try {

            return storageService.descargarArchivo(
                    documento.getClaveArchivo());

        } catch (RuntimeException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo descargar el archivo");
        }
    }

    // Eliminar documento
    public void eliminar(Integer id) {

        Documento documento = obtenerDocumento(id);

        // Eliminar archivo de Object Storage
        try {

            storageService.eliminarArchivo(
                    documento.getClaveArchivo());

        } catch (RuntimeException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo eliminar el archivo");
        }

        // Eliminar registro de PostgreSQL
        documentoRepository.delete(documento);
    }

    // Obtener documento desde la base de datos
    private Documento obtenerDocumento(Integer id) {

        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Documento no encontrado"));
    }

    // Convertir modelo a DTO
    private DocumentoDTO convertirDTO(Documento documento) {

        return new DocumentoDTO(
                documento.getId(),
                documento.getNombre(),
                documento.getDescripcion(),
                documento.getTipoArchivo(),
                documento.getTamano(),
                documento.getFechaSubida(),
                documento.getUsuario().getId()
        );
    }
}