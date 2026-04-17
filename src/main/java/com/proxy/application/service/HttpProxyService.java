package com.proxy.application.service;

import com.proxy.domain.model.ProxyRequest;
import com.proxy.domain.model.RateLimit;
import com.proxy.domain.model.Quota;
import com.proxy.domain.service.ProxyService;
import com.proxy.infrastructure.exception.RateLimitExceededException;
import com.proxy.infrastructure.exception.QuotaExceededException;
import com.proxy.infrastructure.exception.ProxyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
public class HttpProxyService implements ProxyService {
    
    private final HttpClient httpClient;
    private final ConcurrentHashMap<String, RateLimit> rateLimits;
    private final ConcurrentHashMap<String, Quota> quotas;
    
    private static final int MAX_REQUESTS_PER_WINDOW = 100;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 1;
    private static final long MAX_BYTES_PER_HOUR = 10 * 1024 * 1024; // 10MB
    private static final int QUOTA_RESET_HOURS = 1;

    public HttpProxyService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.rateLimits = new ConcurrentHashMap<>();
        this.quotas = new ConcurrentHashMap<>();
    }

    @Override
    public String proxyRequest(ProxyRequest request) {
        if (!checkRateLimit(request.getClientIp())) {
            throw new RateLimitExceededException("Rate limit exceeded for IP: " + request.getClientIp());
        }
        
        if (!checkQuota(request.getClientIp(), 1024)) { // Estimated 1KB
            throw new QuotaExceededException("Quota exceeded for IP: " + request.getClientIp());
        }
        
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(request.getTargetUrl()))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());
            
            updateRateLimit(request.getClientIp());
            updateQuota(request.getClientIp(), response.body().getBytes().length);
            
            return response.body();
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProxyException("Failed to proxy request to: " + request.getTargetUrl(), e);
        }
    }

    @Override
    public boolean checkRateLimit(String clientIp) {
        return rateLimits.computeIfAbsent(clientIp, 
                ip -> new RateLimit(ip, MAX_REQUESTS_PER_WINDOW, RATE_LIMIT_WINDOW_MINUTES))
                .isAllowed();
    }

    @Override
    public boolean checkQuota(String clientIp, long estimatedBytes) {
        return quotas.computeIfAbsent(clientIp, 
                ip -> new Quota(ip, MAX_BYTES_PER_HOUR, QUOTA_RESET_HOURS))
                .isAllowed(estimatedBytes);
    }

    @Override
    public void updateRateLimit(String clientIp) {
        rateLimits.get(clientIp).incrementRequest();
    }

    @Override
    public void updateQuota(String clientIp, long bytes) {
        quotas.get(clientIp).consumeBytes(bytes);
    }
}
