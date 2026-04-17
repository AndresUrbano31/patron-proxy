package com.proxy.application.service;

import com.proxy.domain.model.UserAccount;
import com.proxy.infrastructure.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {
    
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimits = new ConcurrentHashMap<>();
    
    public boolean checkRateLimit(UserAccount userAccount) {
        String key = userAccount.getClientIp();
        RateLimitInfo rateLimit = rateLimits.computeIfAbsent(key, 
                k -> new RateLimitInfo(userAccount.getPlan().getMaxRequestsPerMinute()));
        
        return rateLimit.isAllowed();
    }
    
    public void incrementRequest(UserAccount userAccount) {
        String key = userAccount.getClientIp();
        RateLimitInfo rateLimit = rateLimits.get(key);
        
        if (rateLimit != null) {
            rateLimit.incrementRequest();
        }
    }
    
    public void resetRateLimit(String clientIp) {
        rateLimits.remove(clientIp);
    }
    
    public int getCurrentRequests(String clientIp) {
        RateLimitInfo rateLimit = rateLimits.get(clientIp);
        return rateLimit != null ? rateLimit.getCurrentRequests() : 0;
    }
    
    public int getMaxRequests(String clientIp) {
        RateLimitInfo rateLimit = rateLimits.get(clientIp);
        return rateLimit != null ? rateLimit.getMaxRequests() : 0;
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
        
        public LocalDateTime getWindowStart() {
            return windowStart;
        }
    }
}
