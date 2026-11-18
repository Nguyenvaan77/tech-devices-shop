package com.example.web.service.inter;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String upload(String bucket,String objectKey, MultipartFile file);

    void delete(String bucket, String objectKey);

    String update(String bucket, String oldObjectKey, MultipartFile newFile);

    String getPresignedUrl(String bucket, String objectKey, int expireMinutes);
}
