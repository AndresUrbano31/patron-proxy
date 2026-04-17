package com.proxy.application.service;

import com.proxy.domain.model.UsageHistory;
import com.proxy.domain.model.UserAccount;
import com.proxy.infrastructure.repository.UsageHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UsageHistoryService {
    
    private final UsageHistoryRepository usageHistoryRepository;
    
    public UsageHistoryService(UsageHistoryRepository usageHistoryRepository) {
        this.usageHistoryRepository = usageHistoryRepository;
    }
    
    public UsageHistory recordSuccessfulUsage(UserAccount userAccount, String targetUrl, 
                                            Integer statusCode, Long responseSize, Long responseTime) {
        UsageHistory usage = new UsageHistory(userAccount, targetUrl, statusCode, responseSize, responseTime, true);
        return usageHistoryRepository.save(usage);
    }
    
    public UsageHistory recordFailedUsage(UserAccount userAccount, String targetUrl, String errorMessage) {
        UsageHistory usage = new UsageHistory(userAccount, targetUrl, errorMessage);
        return usageHistoryRepository.save(usage);
    }
    
    @Transactional(readOnly = true)
    public List<UsageHistory> getUserUsageHistory(Long userAccountId) {
        return usageHistoryRepository.findByUserAccountIdOrderByRequestTimeDesc(userAccountId);
    }
    
    @Transactional(readOnly = true)
    public List<UsageHistory> getUserUsageHistoryInPeriod(Long userAccountId, LocalDateTime start, LocalDateTime end) {
        return usageHistoryRepository.findByUserAccountIdAndRequestTimeBetweenOrderByRequestTimeDesc(userAccountId, start, end);
    }
    
    @Transactional(readOnly = true)
    public long getRequestCountByUserSince(Long userAccountId, LocalDateTime since) {
        return usageHistoryRepository.countRequestsByUserSince(userAccountId, since);
    }
    
    @Transactional(readOnly = true)
    public Long getBytesUsedByUserSince(Long userAccountId, LocalDateTime since) {
        return usageHistoryRepository.sumBytesUsedByUserSince(userAccountId, since);
    }
    
    @Transactional(readOnly = true)
    public long getFailedRequestCountSince(LocalDateTime since) {
        return usageHistoryRepository.countFailedRequestsSince(since);
    }
    
    @Transactional(readOnly = true)
    public Double getAverageResponseTimeSince(LocalDateTime since) {
        return usageHistoryRepository.getAverageResponseTimeSince(since);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getMostRequestedUrlsSince(LocalDateTime since) {
        return usageHistoryRepository.findMostRequestedUrlsSince(since);
    }
}
