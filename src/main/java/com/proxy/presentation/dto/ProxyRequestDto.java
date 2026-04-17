package com.proxy.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class ProxyRequestDto {
    @NotBlank(message = "URL is required")
    private String url;

    public ProxyRequestDto() {}

    public ProxyRequestDto(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
