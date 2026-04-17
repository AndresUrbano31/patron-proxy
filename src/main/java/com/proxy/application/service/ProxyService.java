package com.proxy.application.service;

import com.proxy.domain.model.ProxyRequest;
import com.proxy.domain.model.UserAccount;
import com.proxy.domain.model.UsageHistory;
import com.proxy.infrastructure.exception.RateLimitExceededException;
import com.proxy.infrastructure.exception.QuotaExceededException;
import com.proxy.infrastructure.exception.ProxyException;
import com.proxy.infrastructure.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
public class ProxyService {
    
    private final UserAccountService userAccountService;
    private final RateLimitService rateLimitService;
    private final QuotaService quotaService;
    private final UsageHistoryService usageHistoryService;
    private final HttpClient httpClient;
    
    public ProxyService(UserAccountService userAccountService, 
                       RateLimitService rateLimitService,
                       QuotaService quotaService,
                       UsageHistoryService usageHistoryService) {
        this.userAccountService = userAccountService;
        this.rateLimitService = rateLimitService;
        this.quotaService = quotaService;
        this.usageHistoryService = usageHistoryService;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    public ProxyResponse proxyRequest(ProxyRequest request) {
        try {
            // Find or create user account
            UserAccount userAccount = getOrCreateUserAccount(request.getClientIp());
            
            // Check rate limit
            if (!rateLimitService.checkRateLimit(userAccount)) {
                usageHistoryService.recordFailedUsage(userAccount, request.getTargetUrl(), "Rate limit exceeded");
                throw new RateLimitExceededException("Rate limit exceeded for IP: " + request.getClientIp());
            }
            
            // Check quota (estimate 1KB for request)
            if (!quotaService.checkQuotaAvailable(userAccount, 1024)) {
                usageHistoryService.recordFailedUsage(userAccount, request.getTargetUrl(), "Quota exceeded");
                throw new QuotaExceededException("Quota exceeded for IP: " + request.getClientIp());
            }
            
            // Make the actual HTTP request
            long startTime = System.currentTimeMillis();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(request.getTargetUrl()))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Update rate limit and quota
            rateLimitService.incrementRequest(userAccount);
            long responseSize = response.body().getBytes().length;
            quotaService.consumeQuota(userAccount, responseSize);
            
            // Record successful usage
            usageHistoryService.recordSuccessfulUsage(userAccount, request.getTargetUrl(), 
                    response.statusCode(), responseSize, responseTime);
            
            return new ProxyResponse(response.body(), response.statusCode(), responseSize, responseTime);
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProxyException("Failed to proxy request to: " + request.getTargetUrl(), e);
        }
    }
    
    private UserAccount getOrCreateUserAccount(String clientIp) {
        // Try to find existing user by IP
        var existingUser = userAccountService.findUserByClientIp(clientIp);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        // Create new user with default plan
        String email = "user_" + clientIp.replace(".", "_") + "@proxy.local";
        return userAccountService.createUser(email, clientIp, "Basic");
    }
    
    public record ProxyResponse(String content, int statusCode, long responseSize, long responseTime) {}
}
