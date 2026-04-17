package com.proxy.application.service;

import com.proxy.domain.model.Plan;
import com.proxy.domain.model.UserAccount;
import com.proxy.infrastructure.exception.UserNotFoundException;
import com.proxy.infrastructure.exception.UserAlreadyExistsException;
import com.proxy.infrastructure.repository.PlanRepository;
import com.proxy.infrastructure.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserAccountService {
    
    private final UserAccountRepository userAccountRepository;
    private final PlanRepository planRepository;
    
    public UserAccountService(UserAccountRepository userAccountRepository, PlanRepository planRepository) {
        this.userAccountRepository = userAccountRepository;
        this.planRepository = planRepository;
    }
    
    public UserAccount createUser(String email, String clientIp, String planName) {
        if (userAccountRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        
        Optional<Plan> plan = planRepository.findByName(planName);
        if (plan.isEmpty() || !plan.get().getActive()) {
            throw new IllegalArgumentException("Plan " + planName + " not found or inactive");
        }
        
        UserAccount userAccount = new UserAccount(email, clientIp, plan.get());
        return userAccountRepository.save(userAccount);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserAccount> findUserByEmail(String email) {
        return userAccountRepository.findActiveByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserAccount> findUserByClientIp(String clientIp) {
        return userAccountRepository.findActiveByClientIp(clientIp);
    }
    
    public UserAccount upgradePlan(String email, String newPlanName) {
        UserAccount userAccount = findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        
        Optional<Plan> newPlan = planRepository.findByName(newPlanName);
        if (newPlan.isEmpty() || !newPlan.get().getActive()) {
            throw new IllegalArgumentException("Plan " + newPlanName + " not found or inactive");
        }
        
        userAccount.upgradePlan(newPlan.get());
        return userAccountRepository.save(userAccount);
    }
    
    public UserAccount upgradePlanByClientIp(String clientIp, String newPlanName) {
        UserAccount userAccount = findUserByClientIp(clientIp)
                .orElseThrow(() -> new UserNotFoundException("User not found for IP: " + clientIp));
        
        Optional<Plan> newPlan = planRepository.findByName(newPlanName);
        if (newPlan.isEmpty() || !newPlan.get().getActive()) {
            throw new IllegalArgumentException("Plan " + newPlanName + " not found or inactive");
        }
        
        userAccount.upgradePlan(newPlan.get());
        return userAccountRepository.save(userAccount);
    }
    
    @Transactional(readOnly = true)
    public List<UserAccount> findUsersWithPlanChangesSince(LocalDateTime since) {
        return userAccountRepository.findUsersWithPlanChangesSince(since);
    }
    
    @Transactional(readOnly = true)
    public long countActiveUsersByPlan(Long planId) {
        return userAccountRepository.countActiveUsersByPlan(planId);
    }
    
    public void deactivateUser(String email) {
        UserAccount userAccount = findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        
        userAccount.setActive(false);
        userAccountRepository.save(userAccount);
    }
    
    public UserAccount createOrUpdateUser(String email, String clientIp, String planName) {
        Optional<UserAccount> existingUser = findUserByEmail(email);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        return createUser(email, clientIp, planName);
    }
}
