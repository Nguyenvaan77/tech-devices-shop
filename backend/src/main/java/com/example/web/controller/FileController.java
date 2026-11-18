package com.example.web.controller;

import java.nio.file.attribute.FileStoreAttributeView;
import java.util.UUID;

import org.hibernate.service.internal.ProvidedService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.EntityResponse;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.address.request.CreateAddressRequest;
import com.example.web.dto.product.response.ProductResponse;
import com.example.web.dto.productimage.ProductImageDto;
import com.example.web.dto.productimage.request.CreateProductImageRequest;
import com.example.web.dto.productvideo.ProductVideoDTO;
import com.example.web.entity.ProductVideo;
import com.example.web.mapper.ProductVideoMapper;
import com.example.web.service.imple.FileStorageServiceImpl;
import com.example.web.service.inter.FileStorageService;
import com.example.web.service.inter.ProductImageService;
import com.example.web.service.inter.ProductService;
import com.example.web.service.inter.ProductVideoService;
import com.google.common.net.MediaType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final ProductImageService productImageService;
    private final ProductVideoService productVideoService;
    private final ProductVideoMapper productVideoMapper;

    @Value("${minio.bucket.images}")
    private String imageBucket;

    @Value("${minio.bucket.videos}")
    private String videoBucket;

    @PostMapping("/products/{productId}/videos/upload")
    public ResponseEntity<ApiResponse<ProductVideoDTO>> uploadProductMedia(@PathVariable Long productId, @RequestPart("file-video") MultipartFile file) {
        String objectKey = productId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        String publicUrl = fileStorageService.upload(videoBucket, objectKey, file);

        ProductVideoDTO videoDTO = ProductVideoDTO.builder()
            .bucketName(videoBucket)
            .originalFileName(file.getOriginalFilename())
            .fileName(objectKey)
            .fileSize(file.getSize())
            .contentType(file.getContentType())
            .publicUrl(publicUrl)
            .productId(productId)
            .build();

        ProductVideo savedVideo = productVideoService.save(videoDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productVideoMapper.toDTO(savedVideo)));
    }

    @PostMapping("/products/{productId}/images/upload")
    public String uploadProductImage(@PathVariable Long productId, @RequestPart("file") MultipartFile file) {
        String objectKey = productId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        String publicUrl = fileStorageService.upload(imageBucket, objectKey, file);

        ProductImageDto requestImage = ProductImageDto.builder()
            .bucketName(imageBucket)
            .originalFileName(file.getOriginalFilename())
            .fileName(objectKey)
            .fileSize(file.getSize())
            .contentType(file.getContentType())
            .publicUrl(publicUrl)
            .productId(productId)
            .build();

        productImageService.create(requestImage);

        return publicUrl;
    }

    @DeleteMapping("/products/{productId}/videos/{videoId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductVideo(@PathVariable Long productId, @
        PathVariable Long videoId) {
        ProductVideo video = productVideoService.findById(videoId);
        if (video == null || !video.getProduct().getId().equals(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Video not found for this product", HttpStatus.NOT_FOUND.value()));
        }

        fileStorageService.delete(video.getBucketName(), video.getFileName());
        productVideoService.deleteById(videoId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/products/{productId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(@PathVariable Long productId, @PathVariable Long imageId) {
            ProductImageDto image = productImageService.findById(imageId);
            if (image == null || !image.getProductId().equals(productId)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Image not found for this product", HttpStatus.NOT_FOUND.value()));
            }

            fileStorageService.delete(image.getBucketName(), image.getFileName());
            productImageService.deleteById(imageId);
            return ResponseEntity.ok(ApiResponse.success(null));
    }

}
