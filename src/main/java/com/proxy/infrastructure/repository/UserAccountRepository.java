package com.proxy.infrastructure.repository;

import com.proxy.domain.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    
    Optional<UserAccount> findByEmail(String email);
    
    Optional<UserAccount> findByClientIp(String clientIp);
    
    boolean existsByEmail(String email);
    
    boolean existsByClientIp(String clientIp);
    
    @Query("SELECT u FROM UserAccount u WHERE u.active = true AND u.clientIp = :clientIp")
    Optional<UserAccount> findActiveByClientIp(@Param("clientIp") String clientIp);
    
    @Query("SELECT u FROM UserAccount u WHERE u.active = true AND u.email = :email")
    Optional<UserAccount> findActiveByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM UserAccount u WHERE u.planChangedAt >= :since")
    List<UserAccount> findUsersWithPlanChangesSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM UserAccount u WHERE u.active = true AND u.plan.id = :planId")
    long countActiveUsersByPlan(@Param("planId") Long planId);
}
