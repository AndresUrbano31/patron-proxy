package com.proxy.presentation.exception;

import com.proxy.infrastructure.exception.QuotaExceededException;
import com.proxy.infrastructure.exception.RateLimitExceededException;
import com.proxy.infrastructure.exception.ProxyException;
import com.proxy.presentation.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleRateLimitExceeded(RateLimitExceededException e) {
        ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "RATE_LIMIT_EXCEEDED", 429);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);
    }

    @ExceptionHandler(QuotaExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleQuotaExceeded(QuotaExceededException e) {
        ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "QUOTA_EXCEEDED", 429);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);
    }

    @ExceptionHandler(ProxyException.class)
    public ResponseEntity<ErrorResponseDto> handleProxyException(ProxyException e) {
        ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "PROXY_ERROR", 502);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((s1, s2) -> s1 + "; " + s2)
                .orElse("Validation failed");
        
        ErrorResponseDto errorDto = new ErrorResponseDto(message, "VALIDATION_ERROR", 400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        ErrorResponseDto errorDto = new ErrorResponseDto("Internal server error: " + e.getMessage(), "INTERNAL_ERROR", 500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }
}
