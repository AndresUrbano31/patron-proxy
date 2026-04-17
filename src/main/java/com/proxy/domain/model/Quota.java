package com.proxy.domain.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class Quota {
    private final String clientIp;
    private final AtomicLong bytesUsed;
    private final long maxBytes;
    private LocalDateTime resetTime;
    private final int resetHours;

    public Quota(String clientIp, long maxBytes, int resetHours) {
        this.clientIp = clientIp;
        this.bytesUsed = new AtomicLong(0);
        this.maxBytes = maxBytes;
        this.resetHours = resetHours;
        this.resetTime = LocalDateTime.now().plusHours(resetHours);
    }

    public boolean isAllowed(long requestedBytes) {
        resetIfNeeded();
        return (bytesUsed.get() + requestedBytes) <= maxBytes;
    }

    public void consumeBytes(long bytes) {
        resetIfNeeded();
        bytesUsed.addAndGet(bytes);
    }

    private void resetIfNeeded() {
        if (resetTime.isBefore(LocalDateTime.now())) {
            bytesUsed.set(0);
            resetTime = LocalDateTime.now().plusHours(resetHours);
        }
    }

    public long getBytesUsed() {
        return bytesUsed.get();
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    public double getUsagePercentage() {
        return (double) bytesUsed.get() / maxBytes * 100;
    }

    public LocalDateTime getResetTime() {
        return resetTime;
    }
}
