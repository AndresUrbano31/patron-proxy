package com.proxy.application.service;

import com.proxy.domain.model.QuotaUsage;
import com.proxy.domain.model.UserAccount;
import com.proxy.infrastructure.exception.QuotaExceededException;
import com.proxy.infrastructure.repository.QuotaUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class QuotaService {
    
    private final QuotaUsageRepository quotaUsageRepository;
    
    public QuotaService(QuotaUsageRepository quotaUsageRepository) {
        this.quotaUsageRepository = quotaUsageRepository;
    }
    
    public boolean checkQuotaAvailable(UserAccount userAccount, long requestedBytes) {
        Optional<QuotaUsage> currentQuota = getCurrentQuota(userAccount);
        
        if (currentQuota.isEmpty()) {
            createNewQuotaPeriod(userAccount);
            return true;
        }
        
        QuotaUsage quota = currentQuota.get();
        
        if (quota.isExpired()) {
            quotaUsageRepository.delete(quota);
            createNewQuotaPeriod(userAccount);
            return true;
        }
        
        return quota.hasQuotaAvailable(requestedBytes);
    }
    
    public void consumeQuota(UserAccount userAccount, long bytes) {
        Optional<QuotaUsage> currentQuota = getCurrentQuota(userAccount);
        
        if (currentQuota.isEmpty()) {
            throw new QuotaExceededException("No quota period found for user");
        }
        
        QuotaUsage quota = currentQuota.get();
        
        if (quota.isExpired()) {
            quotaUsageRepository.delete(quota);
            createNewQuotaPeriod(userAccount);
            consumeQuota(userAccount, bytes);
            return;
        }
        
        if (!quota.hasQuotaAvailable(bytes)) {
            throw new QuotaExceededException("Quota exceeded for user " + userAccount.getEmail() + 
                    ". Used: " + quota.getBytesUsed() + "/" + quota.getMaxBytes() + 
                    " bytes (" + String.format("%.2f%%", quota.getUsagePercentage()) + ")");
        }
        
        quota.consumeBytes(bytes);
        quotaUsageRepository.save(quota);
    }
    
    @Transactional(readOnly = true)
    public Optional<QuotaUsage> getCurrentQuota(UserAccount userAccount) {
        return quotaUsageRepository.findActiveQuotaForUser(userAccount.getId(), LocalDateTime.now());
    }
    
    @Transactional(readOnly = true)
    public Optional<QuotaUsage> getCurrentQuotaByClientIp(String clientIp) {
        return quotaUsageRepository.findActiveQuotaByClientIp(clientIp, LocalDateTime.now());
    }
    
    private void createNewQuotaPeriod(UserAccount userAccount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusHours(1); // 1-hour quota window
        
        QuotaUsage newQuota = new QuotaUsage(userAccount, userAccount.getPlan().getMaxBytesPerHour(), now, windowEnd);
        quotaUsageRepository.save(newQuota);
    }
    
    public void cleanupExpiredQuotas() {
        LocalDateTime expiredBefore = LocalDateTime.now();
        int deleted = quotaUsageRepository.deleteExpiredQuotas(expiredBefore);
        if (deleted > 0) {
            System.out.println("Cleaned up " + deleted + " expired quota periods");
        }
    }
    
    @Transactional(readOnly = true)
    public double getQuotaUsagePercentage(UserAccount userAccount) {
        Optional<QuotaUsage> currentQuota = getCurrentQuota(userAccount);
        return currentQuota.map(QuotaUsage::getUsagePercentage).orElse(0.0);
    }
}
