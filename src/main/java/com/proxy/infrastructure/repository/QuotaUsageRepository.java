package com.proxy.infrastructure.repository;

import com.proxy.domain.model.QuotaUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotaUsageRepository extends JpaRepository<QuotaUsage, Long> {
    
    Optional<QuotaUsage> findByUserAccountIdAndWindowEndAfter(Long userAccountId, LocalDateTime now);
    
    List<QuotaUsage> findByUserAccountIdOrderByWindowEndDesc(Long userAccountId);
    
    @Query("SELECT q FROM QuotaUsage q WHERE q.userAccount.id = :userAccountId AND q.windowEnd > :now")
    Optional<QuotaUsage> findActiveQuotaForUser(@Param("userAccountId") Long userAccountId, @Param("now") LocalDateTime now);
    
    @Query("SELECT q FROM QuotaUsage q WHERE q.windowEnd < :expiredBefore")
    List<QuotaUsage> findExpiredQuotas(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    @Query("SELECT COUNT(q) FROM QuotaUsage q WHERE q.userAccount.id = :userAccountId AND q.windowStart >= :since")
    long countQuotaPeriodsByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT q FROM QuotaUsage q WHERE q.userAccount.clientIp = :clientIp AND q.windowEnd > :now")
    Optional<QuotaUsage> findActiveQuotaByClientIp(@Param("clientIp") String clientIp, @Param("now") LocalDateTime now);
    
    @Query("DELETE FROM QuotaUsage q WHERE q.windowEnd < :expiredBefore")
    int deleteExpiredQuotas(@Param("expiredBefore") LocalDateTime expiredBefore);
}
