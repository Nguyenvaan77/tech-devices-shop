package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.user.request.LoginRequest;
import com.example.web.dto.user.request.RegisterRequest;
import com.example.web.service.inter.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Object register(@RequestBody RegisterRequest request) {

        return ApiResponse.created(
                userService.register(request)
        );
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {

        return ApiResponse.success(
                userService.login(request)
        );
    }

    @GetMapping("/{id}")
    public Object getUser(@PathVariable Long id) {

        return ApiResponse.success(
                userService.getUser(id)
        );
    }
}