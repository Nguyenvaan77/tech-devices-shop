package com.example.web.service.inter;

import com.example.web.dto.user.request.LoginRequest;
import com.example.web.dto.user.request.RegisterRequest;
import com.example.web.dto.user.response.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);

    String login(LoginRequest request);

    UserResponse getUser(Long id);
}