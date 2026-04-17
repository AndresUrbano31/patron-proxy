package com.proxy.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_usage")
public class TokenUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false)
    private Integer tokensUsed;
    
    @Column(nullable = false)
    private Integer maxTokens;
    
    @Column(name = "window_start", nullable = false)
    private LocalDateTime windowStart;
    
    @Column(name = "window_end", nullable = false)
    private LocalDateTime windowEnd;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TokenUsage() {
        this.createdAt = LocalDateTime.now();
    }

    public TokenUsage(UserAccount userAccount, Integer maxTokens, LocalDateTime windowStart, LocalDateTime windowEnd) {
        this();
        this.userAccount = userAccount;
        this.tokensUsed = 0;
        this.maxTokens = maxTokens;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(windowEnd);
    }

    public boolean hasTokensAvailable(Integer requestedTokens) {
        return (tokensUsed + requestedTokens) <= maxTokens;
    }

    public void consumeTokens(Integer tokens) {
        if (hasTokensAvailable(tokens)) {
            this.tokensUsed += tokens;
        } else {
            throw new RuntimeException("Token quota exceeded");
        }
    }

    public double getUsagePercentage() {
        return (double) tokensUsed / maxTokens * 100;
    }

    public Integer getRemainingTokens() {
        return maxTokens - tokensUsed;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public LocalDateTime getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(LocalDateTime windowEnd) {
        this.windowEnd = windowEnd;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TokenUsage{" +
                "id=" + id +
                ", userAccount=" + (userAccount != null ? userAccount.getEmail() : "null") +
                ", tokensUsed=" + tokensUsed +
                ", maxTokens=" + maxTokens +
                ", remainingTokens=" + getRemainingTokens() +
                ", usagePercentage=" + String.format("%.2f%%", getUsagePercentage()) +
                ", windowEnd=" + windowEnd +
                '}';
    }
}
