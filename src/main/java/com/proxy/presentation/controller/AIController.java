package com.proxy.presentation.controller;

import com.proxy.application.service.QuotaProxyService;
import com.proxy.application.service.RateLimitProxyService;
import com.proxy.application.service.UserAccountService;
import com.proxy.domain.model.UserAccount;
import com.proxy.presentation.dto.AIGenerationRequestDto;
import com.proxy.presentation.dto.AIGenerationResponseDto;
import com.proxy.presentation.dto.ErrorResponseDto;
import com.proxy.infrastructure.exception.QuotaExceededException;
import com.proxy.infrastructure.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    private final QuotaProxyService quotaProxyService;
    private final RateLimitProxyService rateLimitProxyService;
    private final UserAccountService userAccountService;
    
    public AIController(QuotaProxyService quotaProxyService, 
                       RateLimitProxyService rateLimitProxyService,
                       UserAccountService userAccountService) {
        this.quotaProxyService = quotaProxyService;
        this.rateLimitProxyService = rateLimitProxyService;
        this.userAccountService = userAccountService;
    }
    
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("AI Generation Service is running with PostgreSQL integration");
    }
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateContent(@Valid @RequestBody AIGenerationRequestDto requestDto, 
                                           HttpServletRequest httpRequest) {
        try {
            String clientIp = getClientIp(httpRequest);
            
            // Check rate limit first
            if (!rateLimitProxyService.checkRateLimitForUser(clientIp)) {
                ErrorResponseDto errorDto = new ErrorResponseDto("Rate limit exceeded", "RATE_LIMIT_EXCEEDED", 429);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);
            }
            
            // Generate content through proxy chain
            String generatedContent = quotaProxyService.generateContent(requestDto.getPrompt());
            
            // Increment rate limit counter
            rateLimitProxyService.incrementRequestForUser(clientIp);
            
            AIGenerationResponseDto responseDto = new AIGenerationResponseDto(
                generatedContent, 
                quotaProxyService.estimateTokens(generatedContent)
            );
            
            return ResponseEntity.ok(responseDto);
            
        } catch (RateLimitExceededException e) {
            ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "RATE_LIMIT_EXCEEDED", 429);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);
            
        } catch (QuotaExceededException e) {
            ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "QUOTA_EXCEEDED", 429);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);
            
        } catch (Exception e) {
            ErrorResponseDto errorDto = new ErrorResponseDto("Internal server error: " + e.getMessage(), "INTERNAL_ERROR", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
        }
    }
    
    @GetMapping("/user/{email}/quota")
    public ResponseEntity<?> getUserQuota(@PathVariable String email) {
        try {
            UserAccount userAccount = userAccountService.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            
            // This would require adding a method to get token usage percentage
            double usagePercentage = 0.0; // Placeholder - would be implemented in TokenUsageService
            
            QuotaInfoDto quotaInfo = new QuotaInfoDto(
                userAccount.getPlan().getMaxRequestsPerMinute(),
                userAccount.getPlan().getMaxBytesPerHour(),
                usagePercentage
            );
            
            return ResponseEntity.ok(quotaInfo);
            
        } catch (Exception e) {
            ErrorResponseDto errorDto = new ErrorResponseDto("User not found", "USER_NOT_FOUND", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    public record QuotaInfoDto(Integer maxRequestsPerMinute, Long maxBytesPerHour, Double usagePercentage) {}
}
