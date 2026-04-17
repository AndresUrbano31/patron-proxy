package com.proxy.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quota_usage")
public class QuotaUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false)
    private Long bytesUsed;
    
    @Column(nullable = false)
    private Long maxBytes;
    
    @Column(name = "window_start", nullable = false)
    private LocalDateTime windowStart;
    
    @Column(name = "window_end", nullable = false)
    private LocalDateTime windowEnd;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public QuotaUsage() {
        this.createdAt = LocalDateTime.now();
    }

    public QuotaUsage(UserAccount userAccount, Long maxBytes, LocalDateTime windowStart, LocalDateTime windowEnd) {
        this();
        this.userAccount = userAccount;
        this.bytesUsed = 0L;
        this.maxBytes = maxBytes;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(windowEnd);
    }

    public boolean hasQuotaAvailable(long requestedBytes) {
        return (bytesUsed + requestedBytes) <= maxBytes;
    }

    public void consumeBytes(long bytes) {
        if (hasQuotaAvailable(bytes)) {
            this.bytesUsed += bytes;
        } else {
            throw new RuntimeException("Quota exceeded");
        }
    }

    public double getUsagePercentage() {
        return (double) bytesUsed / maxBytes * 100;
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

    public Long getBytesUsed() {
        return bytesUsed;
    }

    public void setBytesUsed(Long bytesUsed) {
        this.bytesUsed = bytesUsed;
    }

    public Long getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(Long maxBytes) {
        this.maxBytes = maxBytes;
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
        return "QuotaUsage{" +
                "id=" + id +
                ", userAccount=" + userAccount.getEmail() +
                ", bytesUsed=" + bytesUsed +
                ", maxBytes=" + maxBytes +
                ", usagePercentage=" + String.format("%.2f%%", getUsagePercentage()) +
                ", windowEnd=" + windowEnd +
                '}';
    }
}
