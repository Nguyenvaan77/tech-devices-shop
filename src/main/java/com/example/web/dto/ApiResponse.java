package com.example.web.dto;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ApiResponse {

    public static ResponseEntity<?> success(Object data, String message, int status) {

        return ResponseEntity.status(status).body(
                Map.of(
                        "status", status,
                        "message", message,
                        "data", data
                )
        );
    }

    public static ResponseEntity<?> success(Object data) {

        return success(data, "OK", 200);
    }

    public static ResponseEntity<?> created(Object data) {

        return success(data, "Resource created", 201);
    }

    public static ResponseEntity<?> noContent() {

        return ResponseEntity.status(204).build();
    }

    public static ResponseEntity<?> error(String message, int code) {

        return ResponseEntity.status(code).body(
                Map.of(
                        "code", code,
                        "message", message
                )
        );
    }
}