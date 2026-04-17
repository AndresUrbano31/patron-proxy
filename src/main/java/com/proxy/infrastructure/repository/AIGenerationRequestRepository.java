package com.proxy.infrastructure.repository;

import com.proxy.domain.model.AIGenerationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AIGenerationRequestRepository extends JpaRepository<AIGenerationRequest, Long> {
    
    List<AIGenerationRequest> findByUserAccountIdOrderByRequestTimeDesc(Long userAccountId);
    
    List<AIGenerationRequest> findByUserAccountIdAndRequestTimeBetweenOrderByRequestTimeDesc(
            Long userAccountId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(r) FROM AIGenerationRequest r WHERE r.userAccount.id = :userAccountId AND r.requestTime >= :since")
    long countRequestsByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT SUM(r.tokensUsed) FROM AIGenerationRequest r WHERE r.userAccount.id = :userAccountId AND r.requestTime >= :since")
    Integer sumTokensUsedByUserSince(@Param("userAccountId") Long userAccountId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(r) FROM AIGenerationRequest r WHERE r.success = false AND r.requestTime >= :since")
    long countFailedRequestsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(r.responseTimeMs) FROM AIGenerationRequest r WHERE r.success = true AND r.requestTime >= :since")
    Double getAverageResponseTimeSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT r.prompt, COUNT(r) as requestCount FROM AIGenerationRequest r WHERE r.requestTime >= :since GROUP BY r.prompt ORDER BY requestCount DESC")
    List<Object[]> findMostRequestedPromptsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT r FROM AIGenerationRequest r WHERE r.userAccount.clientIp = :clientIp AND r.requestTime >= :since ORDER BY r.requestTime DESC")
    List<AIGenerationRequest> findRecentRequestsByClientIp(@Param("clientIp") String clientIp, @Param("since") LocalDateTime since);
}
