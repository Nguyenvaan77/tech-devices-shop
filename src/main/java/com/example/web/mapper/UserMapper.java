package com.example.web.mapper;

import com.example.web.dto.user.UserDTO;
import com.example.web.dto.user.request.RegisterRequest;
import com.example.web.dto.user.response.UserResponse;
import com.example.web.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User entity);

    UserResponse toResponse(User entity);

    User toEntity(UserDTO dto);

    User toEntity(RegisterRequest request);
}