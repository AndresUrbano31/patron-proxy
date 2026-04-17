package com.proxy.domain.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimit {
    private final String clientIp;
    private final AtomicInteger requestCount;
    private LocalDateTime windowStart;
    private final int maxRequests;
    private final int windowSizeMinutes;

    public RateLimit(String clientIp, int maxRequests, int windowSizeMinutes) {
        this.clientIp = clientIp;
        this.requestCount = new AtomicInteger(0);
        this.windowStart = LocalDateTime.now();
        this.maxRequests = maxRequests;
        this.windowSizeMinutes = windowSizeMinutes;
    }

    public boolean isAllowed() {
        resetWindowIfNeeded();
        return requestCount.get() < maxRequests;
    }

    public void incrementRequest() {
        resetWindowIfNeeded();
        requestCount.incrementAndGet();
    }

    private void resetWindowIfNeeded() {
        if (windowStart.plusMinutes(windowSizeMinutes).isBefore(LocalDateTime.now())) {
            requestCount.set(0);
            windowStart = LocalDateTime.now();
        }
    }

    public int getCurrentRequests() {
        return requestCount.get();
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public LocalDateTime getWindowStart() {
        return windowStart;
    }
}
