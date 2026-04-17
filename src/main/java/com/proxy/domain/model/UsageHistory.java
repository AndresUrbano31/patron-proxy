package com.proxy.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_history")
public class UsageHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false)
    private String targetUrl;
    
    @Column(nullable = false)
    private Integer statusCode;
    
    @Column(nullable = false)
    private Long responseSize;
    
    @Column(nullable = false)
    private Long responseTime;
    
    @Column(nullable = false)
    private Boolean success;
    
    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;
    
    @Column(name = "error_message")
    private String errorMessage;

    public UsageHistory() {
        this.requestTime = LocalDateTime.now();
    }

    public UsageHistory(UserAccount userAccount, String targetUrl, Integer statusCode, 
                        Long responseSize, Long responseTime, Boolean success) {
        this();
        this.userAccount = userAccount;
        this.targetUrl = targetUrl;
        this.statusCode = statusCode;
        this.responseSize = responseSize;
        this.responseTime = responseTime;
        this.success = success;
    }

    public UsageHistory(UserAccount userAccount, String targetUrl, String errorMessage) {
        this();
        this.userAccount = userAccount;
        this.targetUrl = targetUrl;
        this.success = false;
        this.errorMessage = errorMessage;
        this.statusCode = 500;
        this.responseSize = 0L;
        this.responseTime = 0L;
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

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(Long responseSize) {
        this.responseSize = responseSize;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "UsageHistory{" +
                "id=" + id +
                ", userAccount=" + userAccount.getEmail() +
                ", targetUrl='" + targetUrl + '\'' +
                ", statusCode=" + statusCode +
                ", responseSize=" + responseSize +
                ", success=" + success +
                ", requestTime=" + requestTime +
                '}';
    }
}
