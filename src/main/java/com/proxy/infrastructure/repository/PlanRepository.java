package com.proxy.infrastructure.repository;

import com.proxy.domain.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    Optional<Plan> findByName(String name);
    
    List<Plan> findByActiveTrue();
    
    @Query("SELECT p FROM Plan p WHERE p.active = true ORDER BY p.price ASC")
    List<Plan> findActivePlansOrderByPrice();
    
    @Query("SELECT p FROM Plan p WHERE p.active = true AND p.maxRequestsPerMinute >= ?1 AND p.maxBytesPerHour >= ?2 ORDER BY p.price ASC")
    List<Plan> findActivePlansByRequirements(Integer minRequests, Long minBytes);
    
    boolean existsByName(String name);
}
