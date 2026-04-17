package com.proxy.application.service;

public interface AIGenerationService {
    String generateContent(String prompt);
    Integer estimateTokens(String content);
}
