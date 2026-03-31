package com.example.web.service.inter;

import com.example.web.dto.reviewimage.request.CreateReviewImageRequest;
import com.example.web.dto.reviewimage.response.ReviewImageResponse;

public interface ReviewImageService {
    ReviewImageResponse create(Long reviewId, CreateReviewImageRequest request);

    void delete(Long id);
}
