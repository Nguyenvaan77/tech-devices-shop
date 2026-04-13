package com.example.web.service.imple;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.web.service.inter.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService  {
    private final MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    public FileStorageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String upload(String bucket,String objectKey, MultipartFile file) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            return minioUrl + "/" + bucket + "/" + objectKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String bucket, String objectKey) {
        try {
            minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public String update(String bucket, String oldObjectKey, MultipartFile newFile) {
        try {
            // Delete old file
            delete(bucket, oldObjectKey);

            // Upload new file with the same object key
            return upload(bucket, oldObjectKey, newFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPresignedUrl(String bucket, String objectKey, int expireMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                io.minio.GetPresignedObjectUrlArgs.builder()
                    .method(io.minio.http.Method.GET)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(expireMinutes * 60) // Convert minutes to seconds
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
        }
    }
}
