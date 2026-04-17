package com.proxy.application.service;

import com.proxy.domain.model.UserAccount;
import com.proxy.domain.model.AIGenerationRequest;
import com.proxy.infrastructure.exception.QuotaExceededException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuotaProxyService implements AIGenerationService {
    
    private final AIGenerationService targetService;
    private final UserAccountService userAccountService;
    private final TokenUsageService tokenUsageService;
    private final AIGenerationRequestService requestService;
    
    public QuotaProxyService(AIGenerationService targetService, 
                           UserAccountService userAccountService,
                           TokenUsageService tokenUsageService,
                           AIGenerationRequestService requestService) {
        this.targetService = targetService;
        this.userAccountService = userAccountService;
        this.tokenUsageService = tokenUsageService;
        this.requestService = requestService;
    }
    
    @Override
    public String generateContent(String prompt) {
        // For demo purposes, we'll use a mock user identification
        // In production, this would come from authentication
        String clientIp = "demo_client";
        
        UserAccount userAccount = getOrCreateUserAccount(clientIp);
        AIGenerationRequest request = new AIGenerationRequest(userAccount, prompt);
        
        try {
            // Check token quota before processing
            Integer estimatedTokens = targetService.estimateTokens(prompt);
            
            if (!tokenUsageService.checkTokenQuotaAvailable(userAccount, estimatedTokens)) {
                request.markAsFailed("Token quota exceeded");
                requestService.saveRequest(request);
                throw new QuotaExceededException("Token quota exceeded for user: " + userAccount.getEmail());
            }
            
            // Process the request
            long startTime = System.currentTimeMillis();
            String generatedContent = targetService.generateContent(prompt);
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Calculate actual tokens used
            Integer actualTokensUsed = targetService.estimateTokens(generatedContent);
            
            // Consume tokens
            tokenUsageService.consumeTokens(userAccount, actualTokensUsed);
            
            // Mark request as completed
            request.markAsCompleted(generatedContent, actualTokensUsed, responseTime);
            requestService.saveRequest(request);
            
            return generatedContent;
            
        } catch (Exception e) {
            if (!(e instanceof QuotaExceededException)) {
                request.markAsFailed(e.getMessage());
                requestService.saveRequest(request);
            }
            throw e;
        }
    }
    
    @Override
    public Integer estimateTokens(String content) {
        return targetService.estimateTokens(content);
    }
    
    private UserAccount getOrCreateUserAccount(String clientIp) {
        return userAccountService.findUserByClientIp(clientIp)
                .orElseGet(() -> {
                    String email = "demo_user_" + clientIp.replace(".", "_") + "@proxy.local";
                    return userAccountService.createUser(email, clientIp, "Basic");
                });
    }
}
