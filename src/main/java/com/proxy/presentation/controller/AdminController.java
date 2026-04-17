package com.proxy.presentation.controller;

import com.proxy.application.service.UserAccountService;
import com.proxy.application.service.UsageHistoryService;
import com.proxy.domain.model.UserAccount;
import com.proxy.domain.model.UsageHistory;
import com.proxy.infrastructure.exception.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final UserAccountService userAccountService;
    private final UsageHistoryService usageHistoryService;
    
    public AdminController(UserAccountService userAccountService, UsageHistoryService usageHistoryService) {
        this.userAccountService = userAccountService;
        this.usageHistoryService = usageHistoryService;
    }
    
    @PostMapping("/users")
    public ResponseEntity<UserAccount> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserAccount user = userAccountService.createUser(request.email(), request.clientIp(), request.planName());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/users/{email}")
    public ResponseEntity<UserAccount> getUser(@PathVariable String email) {
        Optional<UserAccount> user = userAccountService.findUserByEmail(email);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/users/{email}/upgrade")
    public ResponseEntity<UserAccount> upgradeUserPlan(@PathVariable String email, @RequestBody UpgradePlanRequest request) {
        try {
            UserAccount user = userAccountService.upgradePlan(email, request.newPlanName());
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/users/{email}/usage")
    public ResponseEntity<List<UsageHistory>> getUserUsage(@PathVariable String email) {
        Optional<UserAccount> user = userAccountService.findUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<UsageHistory> usage = usageHistoryService.getUserUsageHistory(user.get().getId());
        return ResponseEntity.ok(usage);
    }
    
    @GetMapping("/users/{email}/usage/period")
    public ResponseEntity<List<UsageHistory>> getUserUsageInPeriod(
            @PathVariable String email,
            @RequestParam String start,
            @RequestParam String end) {
        
        Optional<UserAccount> user = userAccountService.findUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);
            
            List<UsageHistory> usage = usageHistoryService.getUserUsageHistoryInPeriod(
                    user.get().getId(), startTime, endTime);
            return ResponseEntity.ok(usage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/stats/summary")
    public ResponseEntity<Map<String, Object>> getStatsSummary() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        
        long totalRequests = usageHistoryService.getRequestCountByUserSince(null, since);
        long failedRequests = usageHistoryService.getFailedRequestCountSince(since);
        Double avgResponseTime = usageHistoryService.getAverageResponseTimeSince(since);
        
        Map<String, Object> stats = Map.of(
                "totalRequests24h", totalRequests,
                "failedRequests24h", failedRequests,
                "successRate24h", totalRequests > 0 ? (double)(totalRequests - failedRequests) / totalRequests * 100 : 0.0,
                "averageResponseTime24h", avgResponseTime != null ? avgResponseTime : 0.0
        );
        
        return ResponseEntity.ok(stats);
    }
    
    public record CreateUserRequest(String email, String clientIp, String planName) {}
    public record UpgradePlanRequest(String newPlanName) {}
}
