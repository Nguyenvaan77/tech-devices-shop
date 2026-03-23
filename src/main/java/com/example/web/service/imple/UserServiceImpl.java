package com.example.web.service.imple;

import com.example.web.dto.auth.LoginRequest;
import com.example.web.dto.auth.RegisterRequest;
import com.example.web.dto.user.response.UserResponse;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ConflictException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.UserMapper;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    

    @Override
    public UserResponse register(RegisterRequest request) {
        if (request == null) {
            throw new BadRequestException("Register request cannot be null");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already registered");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        if (request == null) {
            throw new BadRequestException("Login request cannot be null");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return "LOGIN_SUCCESS";
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }


}