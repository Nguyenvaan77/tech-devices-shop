package com.example.web.service.inter;

import com.example.web.dto.auth.LoginRequest;
import com.example.web.dto.auth.RegisterRequest;
import com.example.web.dto.user.request.UpdateUserRequest;
import com.example.web.dto.user.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse register(RegisterRequest request);

    String login(LoginRequest request);

    UserResponse getUser(Long id);

    UserResponse getCurrentUser();

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}