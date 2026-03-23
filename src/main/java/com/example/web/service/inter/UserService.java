package com.example.web.service.inter;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.web.dto.auth.LoginRequest;
import com.example.web.dto.auth.RegisterRequest;
import com.example.web.dto.user.response.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);

    String login(LoginRequest request);

    UserResponse getUser(Long id);

    List<UserResponse> getAllUsers();

    
}