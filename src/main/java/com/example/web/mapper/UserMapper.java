package com.example.web.mapper;

import com.example.web.dto.auth.RegisterRequest;
import com.example.web.dto.user.UserDTO;
import com.example.web.dto.user.response.UserResponse;
import com.example.web.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDTO toDTO(User entity);

    UserResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequest request);
}