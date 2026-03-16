package com.example.web.service.imple;

import com.example.web.dto.user.request.LoginRequest;
import com.example.web.dto.user.request.RegisterRequest;
import com.example.web.dto.user.response.UserResponse;
import com.example.web.entity.User;
import com.example.web.mapper.UserMapper;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(RegisterRequest request) {

        User user = userMapper.toEntity(request);

        user = userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return "LOGIN_SUCCESS";
    }

    @Override
    public UserResponse getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toResponse(user);
    }
}