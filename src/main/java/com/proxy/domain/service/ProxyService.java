package com.proxy.domain.service;

import com.proxy.domain.model.ProxyRequest;
import com.proxy.domain.model.RateLimit;
import com.proxy.domain.model.Quota;

public interface ProxyService {
    String proxyRequest(ProxyRequest request);
    boolean checkRateLimit(String clientIp);
    boolean checkQuota(String clientIp, long estimatedBytes);
    void updateRateLimit(String clientIp);
    void updateQuota(String clientIp, long bytes);
}
