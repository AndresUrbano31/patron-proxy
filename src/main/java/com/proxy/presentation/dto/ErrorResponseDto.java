package com.proxy.presentation.dto;

import java.time.LocalDateTime;

public class ErrorResponseDto {
    private String message;
    private String errorType;
    private LocalDateTime timestamp;
    private int statusCode;

    public ErrorResponseDto() {}

    public ErrorResponseDto(String message, String errorType, int statusCode) {
        this.message = message;
        this.errorType = errorType;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
