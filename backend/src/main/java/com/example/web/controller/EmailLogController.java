package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-logs")
public class EmailLogController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Object>>> getAll() {
        // TODO: Implement email logs retrieval
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
}
