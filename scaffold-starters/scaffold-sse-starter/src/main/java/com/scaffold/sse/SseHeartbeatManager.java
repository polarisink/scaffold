package com.scaffold.sse;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** 使用 Starter 私有守护线程定时发送心跳，不要求业务应用启用 Spring Scheduling。 */
public final class SseHeartbeatManager implements AutoCloseable {

    private final ScheduledExecutorService scheduler;

    SseHeartbeatManager(SseConnectionManager manager, Duration interval) {
        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "scaffold-sse-heartbeat");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleWithFixedDelay(
                manager::heartbeat, interval.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }
}
