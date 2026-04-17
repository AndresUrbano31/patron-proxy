package com.proxy.application.service;

import com.proxy.domain.model.AIGenerationRequest;
import com.proxy.infrastructure.repository.AIGenerationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AIGenerationRequestService {
    
    private final AIGenerationRequestRepository requestRepository;
    
    public AIGenerationRequestService(AIGenerationRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    
    public AIGenerationRequest saveRequest(AIGenerationRequest request) {
        return requestRepository.save(request);
    }
    
    @Transactional(readOnly = true)
    public List<AIGenerationRequest> getUserRequestHistory(Long userAccountId) {
        return requestRepository.findByUserAccountIdOrderByRequestTimeDesc(userAccountId);
    }
    
    @Transactional(readOnly = true)
    public List<AIGenerationRequest> getUserRequestHistoryInPeriod(Long userAccountId, LocalDateTime start, LocalDateTime end) {
        return requestRepository.findByUserAccountIdAndRequestTimeBetweenOrderByRequestTimeDesc(userAccountId, start, end);
    }
    
    @Transactional(readOnly = true)
    public long getRequestCountByUserSince(Long userAccountId, LocalDateTime since) {
        return requestRepository.countRequestsByUserSince(userAccountId, since);
    }
    
    @Transactional(readOnly = true)
    public Integer getTokensUsedByUserSince(Long userAccountId, LocalDateTime since) {
        return requestRepository.sumTokensUsedByUserSince(userAccountId, since);
    }
    
    @Transactional(readOnly = true)
    public long getFailedRequestCountSince(LocalDateTime since) {
        return requestRepository.countFailedRequestsSince(since);
    }
    
    @Transactional(readOnly = true)
    public Double getAverageResponseTimeSince(LocalDateTime since) {
        return requestRepository.getAverageResponseTimeSince(since);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getMostRequestedPromptsSince(LocalDateTime since) {
        return requestRepository.findMostRequestedPromptsSince(since);
    }
    
    @Transactional(readOnly = true)
    public Optional<AIGenerationRequest> findRequestById(Long id) {
        return requestRepository.findById(id);
    }
}
