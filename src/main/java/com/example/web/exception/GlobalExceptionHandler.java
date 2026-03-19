package com.example.web.exception;

import com.example.web.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleApException(AppException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.error(e.getMessage(), e.getStatus()));
    }

    // 2. Validation error
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(message, 400));
    }

    // 3. Illegal argument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e.getMessage(), 400));
    }

    // 4. Catch ALL (system exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {

        // log lỗi (production dùng logger)
        e.printStackTrace();

        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("Internal Server Error", 500));
    }
}
