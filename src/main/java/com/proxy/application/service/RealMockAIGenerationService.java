package com.proxy.application.service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RealMockAIGenerationService implements AIGenerationService {
    
    private final Random random = new Random();
    
    @Override
    public String generateContent(String prompt) {
        // Simulate AI processing time
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate mock AI content based on prompt
        return generateMockResponse(prompt);
    }
    
    @Override
    public Integer estimateTokens(String content) {
        // Simple token estimation: ~4 characters per token
        return Math.max(1, content.length() / 4);
    }
    
    private String generateMockResponse(String prompt) {
        String[] responses = {
            "Based on your prompt about '" + prompt + "', I can provide you with comprehensive insights and analysis.",
            "Thank you for your inquiry: '" + prompt + "'. Here's my detailed response with actionable recommendations.",
            "Regarding '" + prompt + "', I've analyzed the key aspects and prepared a thorough explanation.",
            "Your prompt '" + prompt + "' raises interesting points. Let me elaborate on each one systematically.",
            "I understand you're asking about '" + prompt + "'. Here's a well-structured response with examples."
        };
        
        // Add some random variation
        String baseResponse = responses[random.nextInt(responses.length)];
        String additionalContent = switch (random.nextInt(5)) {
            case 0 -> " This approach offers several advantages including scalability and maintainability.";
            case 1 -> " Consider implementing best practices to ensure optimal performance.";
            case 2 -> " The solution addresses common challenges while maintaining flexibility.";
            case 3 -> " This method has been proven effective in various scenarios and use cases.";
            case 4 -> " Implementation details may vary based on specific requirements and constraints.";
            default -> " Further optimization opportunities exist for advanced use cases.";
        };
        
        return baseResponse + additionalContent;
    }
}
