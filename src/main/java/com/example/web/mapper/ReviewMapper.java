package com.example.web.mapper;

import com.example.web.dto.review.request.CreateReviewRequest;
import com.example.web.dto.review.response.ReviewResponse;
import com.example.web.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    ReviewResponse toResponse(Review entity);

    Review toEntity(CreateReviewRequest request);
}