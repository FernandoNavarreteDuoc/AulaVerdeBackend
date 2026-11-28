package com.aulaverde.aulaverde.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    private final S3Client s3Client;

    @Value("${storage.bucket}")
    private String bucket;

    public StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // Verificar conexión con Object Storage
    public void verificarConexion() {

        s3Client.headBucket(
                HeadBucketRequest.builder()
                        .bucket(bucket)
                        .build()
        );
    }

    // Subir archivo
    public String subirArchivo(MultipartFile archivo)
            throws IOException {

        // Generar una clave única para el archivo
        String claveArchivo = UUID.randomUUID().toString();

        PutObjectRequest solicitud = PutObjectRequest.builder()
                .bucket(bucket)
                .key(claveArchivo)
                .contentType(archivo.getContentType())
                .build();

        try (var contenido = archivo.getInputStream()) {

            s3Client.putObject(
                    solicitud,
                    RequestBody.fromInputStream(
                            contenido, archivo.getSize())
            );
        }

        return claveArchivo;
    }

    // Descargar archivo
    public byte[] descargarArchivo(String claveArchivo) {

        GetObjectRequest solicitud = GetObjectRequest.builder()
                .bucket(bucket)
                .key(claveArchivo)
                .build();

        return s3Client.getObjectAsBytes(solicitud)
                .asByteArray();
    }

    // Eliminar archivo
    public void eliminarArchivo(String claveArchivo) {

        DeleteObjectRequest solicitud = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(claveArchivo)
                .build();

        s3Client.deleteObject(solicitud);
    }
}