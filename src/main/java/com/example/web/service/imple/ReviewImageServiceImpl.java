package com.example.web.service.imple;

import com.example.web.dto.reviewimage.request.CreateReviewImageRequest;
import com.example.web.dto.reviewimage.response.ReviewImageResponse;
import com.example.web.entity.Review;
import com.example.web.entity.ReviewImage;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.repository.ReviewImageRepository;
import com.example.web.repository.ReviewRepository;
import com.example.web.service.inter.ReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewImageServiceImpl implements ReviewImageService {

        private final ReviewImageRepository reviewImageRepository;
        private final ReviewRepository reviewRepository;

        @Override
        public ReviewImageResponse create(Long reviewId, CreateReviewImageRequest request) {
                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Review not found with id: " + reviewId));

                ReviewImage reviewImage = ReviewImage.builder()
                                .bucketName(request.getBucketName())
                                .originalFileName(request.getOriginalFileName())
                                .fileName(request.getFileName())
                                .fileSize(request.getFileSize())
                                .contentType(request.getContentType())
                                .publicUrl(request.getPublicUrl())
                                .review(review)
                                .build();

                ReviewImage saved = reviewImageRepository.save(reviewImage);
                return ReviewImageResponse.builder()
                                .id(saved.getId())
                                .bucketName(saved.getBucketName())
                                .originalFileName(saved.getOriginalFileName())
                                .fileName(saved.getFileName())
                                .fileSize(saved.getFileSize())
                                .contentType(saved.getContentType())
                                .publicUrl(saved.getPublicUrl())
                                .reviewId(saved.getReview().getId())
                                .build();
        }

        @Override
        public void delete(Long id) {
                ReviewImage reviewImage = reviewImageRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "ReviewImage not found with id: " + id));
                reviewImageRepository.delete(reviewImage);
        }
}
