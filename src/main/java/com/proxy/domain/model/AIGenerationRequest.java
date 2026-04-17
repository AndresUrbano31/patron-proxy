package com.proxy.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_generation_requests")
public class AIGenerationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;
    
    @Column(nullable = false)
    private String prompt;
    
    @Column(nullable = false)
    private String generatedContent;
    
    @Column(nullable = false)
    private Integer tokensUsed;
    
    @Column(nullable = false)
    private Long responseTimeMs;
    
    @Column(nullable = false)
    private Boolean success;
    
    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;
    
    @Column(name = "error_message")
    private String errorMessage;

    public AIGenerationRequest() {
        this.requestTime = LocalDateTime.now();
        this.success = false;
        this.tokensUsed = 0;
        this.responseTimeMs = 0L;
    }

    public AIGenerationRequest(UserAccount userAccount, String prompt) {
        this();
        this.userAccount = userAccount;
        this.prompt = prompt;
    }

    public void markAsCompleted(String generatedContent, Integer tokensUsed, Long responseTimeMs) {
        this.generatedContent = generatedContent;
        this.tokensUsed = tokensUsed;
        this.responseTimeMs = responseTimeMs;
        this.success = true;
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
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

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(String generatedContent) {
        this.generatedContent = generatedContent;
    }

    public Integer getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
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
        return "AIGenerationRequest{" +
                "id=" + id +
                ", userAccount=" + (userAccount != null ? userAccount.getEmail() : "null") +
                ", prompt='" + prompt + '\'' +
                ", tokensUsed=" + tokensUsed +
                ", success=" + success +
                ", requestTime=" + requestTime +
                '}';
    }
}
