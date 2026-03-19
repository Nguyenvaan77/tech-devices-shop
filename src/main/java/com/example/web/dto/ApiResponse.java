package com.example.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Getter
@Builder
@Schema(description = "Standard API response wrapper for all endpoints")
public class ApiResponse<T> {
    @Schema(description = "HTTP status code", example = "200")
    private int status;

    @Schema(description = "Response message", example = "OK")
    private String message;

    @Schema(description = "Response data payload")
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