package com.aulaverde.aulaverde.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Service
public class StorageService {

    private final S3Client s3Client;

    @Value("${storage.bucket}")
    private String bucket;

    public StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void verificarConexion() {

        s3Client.headBucket(
            HeadBucketRequest.builder()
                .bucket(bucket)
                .build()
        );
    }
}