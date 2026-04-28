package com.example.web.exception;

import com.example.web.dto.ApiResponse;
import com.example.web.exception.redis.RedisException;
import com.example.web.exception.redis.RedisUnconnectedException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        // 1. Handle AppException and its subclasses
        @ExceptionHandler(AppException.class)
        public ResponseEntity<ApiResponse<?>> handleAppException(AppException e) {
                return ResponseEntity
                                .status(e.getStatus())
                                .body(ApiResponse.error(e.getMessage(), e.getStatus()));
        }

        // 2. Validation error from @Valid
        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<?>> handleValidationException(
                        org.springframework.web.bind.MethodArgumentNotValidException e) {
                String message = e.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Validation error");

                return ResponseEntity
                                .badRequest()
                                .body(ApiResponse.error(message, 400));
        }

        // 3. Illegal argument
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
                logger.warn("Illegal argument: {}", e.getMessage());
                return ResponseEntity
                                .badRequest()
                                .body(ApiResponse.error(e.getMessage(), 400));
        }

        // 4. Resource not found
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException e) {
                return ResponseEntity
                                .status(404)
                                .body(ApiResponse.error(e.getMessage(), 404));
        }

        // 5. Bad request
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<?>> handleBadRequest(BadRequestException e) {
                return ResponseEntity
                                .badRequest()
                                .body(ApiResponse.error(e.getMessage(), 400));
        }

        // 6. Validation exception
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ApiResponse<?>> handleValidation(ValidationException e) {
                return ResponseEntity
                                .badRequest()
                                .body(ApiResponse.error(e.getMessage(), 400));
        }

        // 7. Unauthorized
        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse<?>> handleUnauthorized(UnauthorizedException e) {
                return ResponseEntity
                                .status(401)
                                .body(ApiResponse.error(e.getMessage(), 401));
        }

        // 8. Forbidden
        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiResponse<?>> handleForbidden(ForbiddenException e) {
                return ResponseEntity
                                .status(403)
                                .body(ApiResponse.error(e.getMessage(), 403));
        }

        // 9. Conflict
        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiResponse<?>> handleConflict(ConflictException e) {
                return ResponseEntity
                                .status(409)
                                .body(ApiResponse.error(e.getMessage(), 409));
        }

        // 10. Internal server error
        @ExceptionHandler(InternalServerException.class)
        public ResponseEntity<ApiResponse<?>> handleInternalServerError(InternalServerException e) {
                logger.error("Internal server error: {}", e.getMessage(), e);
                return ResponseEntity
                                .status(500)
                                .body(ApiResponse.error(e.getMessage(), 500));
        }
        
        // Redis exception
        // 11. Redis server Connection error 
        @ExceptionHandler(RedisUnconnectedException.class)
        public ResponseEntity<ApiResponse<?>> handleRedisServerUnconnectedError(RedisUnconnectedException e) {
                logger.error("Redis connection error: {}", e.getMessage(), e);
                return ResponseEntity
                                .status(500)
                                .body(ApiResponse.error(e.getMessage(), 500));
        }

        // 11. Redis error 
        @ExceptionHandler(RedisException.class)
        public ResponseEntity<ApiResponse<?>> handleRedisError(RedisException e) {
                logger.error("Redis error: {}", e.getMessage(), e);
                return ResponseEntity
                                .status(500)
                                .body(ApiResponse.error(e.getMessage(), 500));
        }

        // 11. Catch ALL (system exception)
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
                logger.error("Unexpected error: {}", e.getMessage(), e);
                return ResponseEntity
                                .status(500)
                                .body(ApiResponse.error("Internal Server Error", 500));
        }
}
