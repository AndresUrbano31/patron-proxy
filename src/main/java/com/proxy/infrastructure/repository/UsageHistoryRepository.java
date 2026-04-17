package com.proxy.infrastructure.repository;

import com.proxy.domain.model.UsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Long> {
    
    List<UsageHistory> findByUserAccountIdOrderByRequestTimeDesc(Long userAccountId);
    
    List<UsageHistory> findByUserAccountIdAndRequestTimeBetweenOrderByRequestTimeDesc(
            Long userAccountId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(u) FROM UsageHistory u WHERE u.userAccount.id = :userAccountId AND u.requestTime >= :since")
    long countRequestsByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT SUM(u.responseSize) FROM UsageHistory u WHERE u.userAccount.id = :userAccountId AND u.requestTime >= :since")
    Long sumBytesUsedByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM UsageHistory u WHERE u.success = false AND u.requestTime >= :since")
    long countFailedRequestsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT u.targetUrl, COUNT(u) as requestCount FROM UsageHistory u WHERE u.requestTime >= :since GROUP BY u.targetUrl ORDER BY requestCount DESC")
    List<Object[]> findMostRequestedUrlsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(u.responseTime) FROM UsageHistory u WHERE u.success = true AND u.requestTime >= :since")
    Double getAverageResponseTimeSince(@Param("since") LocalDateTime since);
}
