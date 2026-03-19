package com.example.web.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Getter
@Builder
public class ApiResponse <T> {
    private int status;
    private String message;
    private T data;

    // ===== SUCCESS =====
    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "OK", 200);
    }

    public static <T> ApiResponse<T> created(T data) {
        return success(data, "Resource created", 201);
    }

    public static <T> ApiResponse<T> noContent() {

        return ApiResponse.<T>builder()
                .status(204)
                .message("No content")
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int status) {

        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}