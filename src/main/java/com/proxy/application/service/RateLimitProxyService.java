package com.proxy.application.service;

import com.proxy.domain.model.UserAccount;
import com.proxy.infrastructure.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitProxyService implements AIGenerationService {
    
    private final AIGenerationService targetService;
    private final UserAccountService userAccountService;
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimits = new ConcurrentHashMap<>();
    
    public RateLimitProxyService(AIGenerationService targetService, UserAccountService userAccountService) {
        this.targetService = targetService;
        this.userAccountService = userAccountService;
    }
    
    @Override
    public String generateContent(String prompt) {
        // This would be called with user identification in a real scenario
        // For now, we'll implement a simple rate limit per prompt
        String key = "global_rate_limit";
        
        RateLimitInfo rateLimit = rateLimits.computeIfAbsent(key, 
                k -> new RateLimitInfo(100)); // 100 requests per minute globally
        
        if (!rateLimit.isAllowed()) {
            throw new RateLimitExceededException("Global rate limit exceeded. Please try again later.");
        }
        
        rateLimit.incrementRequest();
        return targetService.generateContent(prompt);
    }
    
    @Override
    public Integer estimateTokens(String content) {
        return targetService.estimateTokens(content);
    }
    
    public boolean checkRateLimitForUser(String clientIp) {
        UserAccount userAccount = userAccountService.findUserByClientIp(clientIp).orElse(null);
        if (userAccount == null) {
            return true; // Allow unknown users for now
        }
        
        String key = userAccount.getClientIp();
        RateLimitInfo rateLimit = rateLimits.computeIfAbsent(key, 
                k -> new RateLimitInfo(userAccount.getPlan().getMaxRequestsPerMinute()));
        
        return rateLimit.isAllowed();
    }
    
    public void incrementRequestForUser(String clientIp) {
        UserAccount userAccount = userAccountService.findUserByClientIp(clientIp).orElse(null);
        if (userAccount == null) {
            return;
        }
        
        String key = userAccount.getClientIp();
        RateLimitInfo rateLimit = rateLimits.get(key);
        if (rateLimit != null) {
            rateLimit.incrementRequest();
        }
    }
    
    private static class RateLimitInfo {
        private final AtomicInteger requestCount;
        private final int maxRequests;
        private LocalDateTime windowStart;
        private static final int WINDOW_SIZE_MINUTES = 1;
        
        public RateLimitInfo(int maxRequests) {
            this.maxRequests = maxRequests;
            this.requestCount = new AtomicInteger(0);
            this.windowStart = LocalDateTime.now();
        }
        
        public boolean isAllowed() {
            resetWindowIfNeeded();
            return requestCount.get() < maxRequests;
        }
        
        public void incrementRequest() {
            resetWindowIfNeeded();
            requestCount.incrementAndGet();
        }
        
        private void resetWindowIfNeeded() {
            if (windowStart.plusMinutes(WINDOW_SIZE_MINUTES).isBefore(LocalDateTime.now())) {
                requestCount.set(0);
                windowStart = LocalDateTime.now();
            }
        }
        
        public int getCurrentRequests() {
            return requestCount.get();
        }
        
        public int getMaxRequests() {
            return maxRequests;
        }
    }
}
