package com.proxy.presentation.dto;

import java.time.LocalDateTime;

public class ProxyResponseDto {
    private String content;
    private int statusCode;
    private LocalDateTime timestamp;
    private long responseSize;

    public ProxyResponseDto() {}

    public ProxyResponseDto(String content, int statusCode, long responseSize) {
        this.content = content;
        this.statusCode = statusCode;
        this.responseSize = responseSize;
        this.timestamp = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(long responseSize) {
        this.responseSize = responseSize;
    }
}
