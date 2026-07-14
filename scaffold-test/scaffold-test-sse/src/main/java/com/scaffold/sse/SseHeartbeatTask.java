package com.scaffold.sse;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SseHeartbeatTask {

    private final SseConnectionManager connectionManager;

    public SseHeartbeatTask(SseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Scheduled(fixedDelayString = "${scaffold.sse.heartbeat-interval:25000}")
    public void heartbeat() {
        connectionManager.heartbeat();
    }
}
