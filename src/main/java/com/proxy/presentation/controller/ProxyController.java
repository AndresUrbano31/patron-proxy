package com.proxy.presentation.controller;

import com.proxy.application.service.ProxyService;
import com.proxy.domain.model.ProxyRequest;
import com.proxy.presentation.dto.ErrorResponseDto;
import com.proxy.presentation.dto.ProxyRequestDto;
import com.proxy.presentation.dto.ProxyResponseDto;
import com.proxy.infrastructure.exception.QuotaExceededException;
import com.proxy.infrastructure.exception.RateLimitExceededException;
import com.proxy.infrastructure.exception.ProxyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @GetMapping
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Proxy Service with PostgreSQL is running - " + LocalDateTime.now());
    }

    @PostMapping
    public ResponseEntity<?> proxyRequest(@Valid @RequestBody ProxyRequestDto requestDto, 
                                         HttpServletRequest httpRequest) {
        try {
            String clientIp = getClientIp(httpRequest);
            ProxyRequest proxyRequest = new ProxyRequest(
                    UUID.randomUUID().toString(),
                    requestDto.getUrl(),
                    clientIp,
                    LocalDateTime.now(),
                    "POST",
                    httpRequest.getHeader("User-Agent")
            );

            ProxyService.ProxyResponse response = proxyService.proxyRequest(proxyRequest);
            ProxyResponseDto responseDto = new ProxyResponseDto(
                    response.content(), 
                    response.statusCode(), 
                    response.responseSize()
            );

            return ResponseEntity.ok(responseDto);

        } catch (RateLimitExceededException e) {
            ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "RATE_LIMIT_EXCEEDED", 429);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);

        } catch (QuotaExceededException e) {
            ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "QUOTA_EXCEEDED", 429);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorDto);

        } catch (ProxyException e) {
            ErrorResponseDto errorDto = new ErrorResponseDto(e.getMessage(), "PROXY_ERROR", 502);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorDto);

        } catch (Exception e) {
            ErrorResponseDto errorDto = new ErrorResponseDto("Internal server error: " + e.getMessage(), "INTERNAL_ERROR", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
