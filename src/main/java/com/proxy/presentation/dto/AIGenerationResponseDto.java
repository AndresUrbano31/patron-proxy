package com.proxy.presentation.dto;

import java.time.LocalDateTime;

public class AIGenerationResponseDto {
    private String generatedContent;
    private Integer tokensUsed;
    private LocalDateTime timestamp;

    public AIGenerationResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public AIGenerationResponseDto(String generatedContent, Integer tokensUsed) {
        this();
        this.generatedContent = generatedContent;
        this.tokensUsed = tokensUsed;
    }

    public String getGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(String generatedContent) {
        this.generatedContent = generatedContent;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
