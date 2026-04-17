package com.proxy.domain.model;

import java.time.LocalDateTime;

public class ProxyRequest {
    private final String id;
    private final String targetUrl;
    private final String clientIp;
    private final LocalDateTime requestTime;
    private final String method;
    private final String userAgent;

    public ProxyRequest(String id, String targetUrl, String clientIp, LocalDateTime requestTime, String method, String userAgent) {
        this.id = id;
        this.targetUrl = targetUrl;
        this.clientIp = clientIp;
        this.requestTime = requestTime;
        this.method = method;
        this.userAgent = userAgent;
    }

    public String getId() {
        return id;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getClientIp() {
        return clientIp;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public String getMethod() {
        return method;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
