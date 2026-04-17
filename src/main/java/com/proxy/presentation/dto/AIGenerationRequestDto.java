package com.proxy.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AIGenerationRequestDto {
    @NotBlank(message = "Prompt is required")
    @Size(min = 1, max = 1000, message = "Prompt must be between 1 and 1000 characters")
    private String prompt;

    public AIGenerationRequestDto() {}

    public AIGenerationRequestDto(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
