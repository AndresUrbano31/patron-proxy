package com.proxy.infrastructure.repository;

import com.proxy.domain.model.TokenUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenUsageRepository extends JpaRepository<TokenUsage, Long> {
    
    Optional<TokenUsage> findByUserAccountIdAndWindowEndAfter(Long userAccountId, LocalDateTime now);
    
    List<TokenUsage> findByUserAccountIdOrderByWindowEndDesc(Long userAccountId);
    
    @Query("SELECT t FROM TokenUsage t WHERE t.userAccount.id = :userAccountId AND t.windowEnd > :now")
    Optional<TokenUsage> findActiveTokenUsageForUser(@Param("userAccountId") Long userAccountId, @Param("now") LocalDateTime now);
    
    @Query("SELECT t FROM TokenUsage t WHERE t.windowEnd < :expiredBefore")
    List<TokenUsage> findExpiredTokenUsages(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    @Query("SELECT COUNT(t) FROM TokenUsage t WHERE t.userAccount.id = :userAccountId AND t.windowStart >= :since")
    long countTokenPeriodsByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM TokenUsage t WHERE t.userAccount.clientIp = :clientIp AND t.windowEnd > :now")
    Optional<TokenUsage> findActiveTokenUsageByClientIp(@Param("clientIp") String clientIp, @Param("now") LocalDateTime now);
    
    @Query("DELETE FROM TokenUsage t WHERE t.windowEnd < :expiredBefore")
    int deleteExpiredTokenUsages(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    @Query("SELECT COUNT(t) FROM TokenUsage t WHERE t.tokensUsed >= t.maxTokens AND t.windowEnd > :now")
    long countUsersWithExhaustedTokens(@Param("now") LocalDateTime now);
}
