package com.example.web.service.imple;

import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.dto.review.response.ReviewResponse;
import com.example.web.entity.Product;
import com.example.web.entity.Review;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.ReviewMapper;
import com.example.web.repository.ProductRepository;
import com.example.web.repository.ReviewRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null) {
            throw new BadRequestException("Review request cannot be null");
        }

        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new BadRequestException("Invalid product ID");
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new BadRequestException("Comment is required");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Review review = reviewMapper.toEntity(request);
        review.setProduct(product);
        review.setUser(user);

        review = reviewRepository.save(review);

        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BadRequestException("Invalid product ID");
        }

        // Verify product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        return reviewRepository.findByProductId(productId)
                .stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());
    }
}