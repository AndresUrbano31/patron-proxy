package com.proxy.application.service;

import com.proxy.domain.model.TokenUsage;
import com.proxy.domain.model.UserAccount;
import com.proxy.infrastructure.exception.QuotaExceededException;
import com.proxy.infrastructure.repository.TokenUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class TokenUsageService {
    
    private final TokenUsageRepository tokenUsageRepository;
    
    public TokenUsageService(TokenUsageRepository tokenUsageRepository) {
        this.tokenUsageRepository = tokenUsageRepository;
    }
    
    public boolean checkTokenQuotaAvailable(UserAccount userAccount, Integer requestedTokens) {
        Optional<TokenUsage> currentTokenUsage = getCurrentTokenUsage(userAccount);
        
        if (currentTokenUsage.isEmpty()) {
            createNewTokenPeriod(userAccount);
            return true;
        }
        
        TokenUsage tokenUsage = currentTokenUsage.get();
        
        if (tokenUsage.isExpired()) {
            tokenUsageRepository.delete(tokenUsage);
            createNewTokenPeriod(userAccount);
            return true;
        }
        
        return tokenUsage.hasTokensAvailable(requestedTokens);
    }
    
    public void consumeTokens(UserAccount userAccount, Integer tokens) {
        Optional<TokenUsage> currentTokenUsage = getCurrentTokenUsage(userAccount);
        
        if (currentTokenUsage.isEmpty()) {
            throw new QuotaExceededException("No token usage period found for user");
        }
        
        TokenUsage tokenUsage = currentTokenUsage.get();
        
        if (tokenUsage.isExpired()) {
            tokenUsageRepository.delete(tokenUsage);
            createNewTokenPeriod(userAccount);
            consumeTokens(userAccount, tokens);
            return;
        }
        
        if (!tokenUsage.hasTokensAvailable(tokens)) {
            throw new QuotaExceededException("Token quota exceeded for user " + userAccount.getEmail() + 
                    ". Used: " + tokenUsage.getTokensUsed() + "/" + tokenUsage.getMaxTokens() + 
                    " tokens (" + String.format("%.2f%%", tokenUsage.getUsagePercentage()) + ")");
        }
        
        tokenUsage.consumeTokens(tokens);
        tokenUsageRepository.save(tokenUsage);
    }
    
    @Transactional(readOnly = true)
    public Optional<TokenUsage> getCurrentTokenUsage(UserAccount userAccount) {
        return tokenUsageRepository.findActiveTokenUsageForUser(userAccount.getId(), LocalDateTime.now());
    }
    
    @Transactional(readOnly = true)
    public Optional<TokenUsage> getCurrentTokenUsageByClientIp(String clientIp) {
        return tokenUsageRepository.findActiveTokenUsageByClientIp(clientIp, LocalDateTime.now());
    }
    
    private void createNewTokenPeriod(UserAccount userAccount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusHours(1); // 1-hour token window
        
        // Use plan's max requests per minute as a proxy for token limit
        // In a real system, this would be a separate token limit field
        Integer maxTokens = userAccount.getPlan().getMaxRequestsPerMinute() * 10; // Rough estimate
        
        TokenUsage newTokenUsage = new TokenUsage(userAccount, maxTokens, now, windowEnd);
        tokenUsageRepository.save(newTokenUsage);
    }
    
    public void cleanupExpiredTokenUsages() {
        LocalDateTime expiredBefore = LocalDateTime.now();
        int deleted = tokenUsageRepository.deleteExpiredTokenUsages(expiredBefore);
        if (deleted > 0) {
            System.out.println("Cleaned up " + deleted + " expired token usage periods");
        }
    }
    
    @Transactional(readOnly = true)
    public double getTokenUsagePercentage(UserAccount userAccount) {
        Optional<TokenUsage> currentTokenUsage = getCurrentTokenUsage(userAccount);
        return currentTokenUsage.map(TokenUsage::getUsagePercentage).orElse(0.0);
    }
}
