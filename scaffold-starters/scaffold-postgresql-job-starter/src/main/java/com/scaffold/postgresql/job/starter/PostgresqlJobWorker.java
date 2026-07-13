package com.scaffold.postgresql.job.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class PostgresqlJobWorker {

    private final PostgresqlJobStore jobStore;
    private final ScaffoldJobProperties properties;
    private final List<PostgresqlJobHandler> handlers;
    private final Object signal = new Object();

    private Map<String, PostgresqlJobHandler> handlerMap = Map.of();
    private ExecutorService executorService;
    private volatile boolean running;

    public void start() {
        if (!properties.isEnabled() || !properties.getWorker().isEnabled()) {
            running = true;
            return;
        }
        handlerMap = buildHandlerMap();
        if (handlerMap.isEmpty()) {
            log.info("PostgreSQL job worker disabled because no PostgresqlJobHandler beans were found");
            running = true;
            return;
        }
        running = true;
        int threads = Math.max(1, properties.getWorker().getThreads());
        executorService = Executors.newFixedThreadPool(threads, threadFactory());
        for (int i = 0; i < threads; i++) {
            executorService.execute(this::runLoop);
        }
    }

    public void stop() {
        running = false;
        wakeup();
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                executorService.shutdownNow();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void wakeup() {
        synchronized (signal) {
            signal.notifyAll();
        }
    }

    private void runLoop() {
        String workerId = workerId();
        while (running) {
            try {
                Optional<PostgresqlJob> job = jobStore.claimNext(queueNames(), workerId, properties.getWorker().getVisibilityTimeout());
                if (job.isPresent()) {
                    handle(job.get(), workerId);
                    continue;
                }
                waitForWork();
            } catch (RuntimeException ex) {
                log.warn("PostgreSQL job worker loop failed: {}", ex.getMessage(), ex);
                waitForWork();
            }
        }
    }

    private void handle(PostgresqlJob job, String workerId) {
        PostgresqlJobHandler handler = handlerMap.get(job.getQueueName());
        if (handler == null) {
            jobStore.fail(job, workerId, "No PostgresqlJobHandler for queue: " + job.getQueueName(), retryDelay(job));
            return;
        }
        try {
            handler.handle(job);
            jobStore.complete(job.getId(), workerId);
        } catch (Exception ex) {
            String message = ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage();
            log.warn("PostgreSQL job {} handle failed: {}", job.getId(), message, ex);
            jobStore.fail(job, workerId, message, retryDelay(job));
        }
    }

    private Map<String, PostgresqlJobHandler> buildHandlerMap() {
        Map<String, PostgresqlJobHandler> map = new HashMap<>();
        for (PostgresqlJobHandler handler : handlers) {
            PostgresqlJobHandler previous = map.put(handler.queueName(), handler);
            if (previous != null) {
                throw new IllegalStateException("Duplicate PostgresqlJobHandler for queue: " + handler.queueName());
            }
        }
        return Map.copyOf(map);
    }

    private List<String> queueNames() {
        List<String> queues = properties.getWorker().getQueues();
        if (queues != null && !queues.isEmpty()) {
            return queues;
        }
        return handlerMap.keySet().stream().toList();
    }

    private Duration retryDelay(PostgresqlJob job) {
        Duration retryDelay = properties.getWorker().getRetryDelay();
        if (retryDelay == null || retryDelay.isNegative() || retryDelay.isZero()) {
            retryDelay = Duration.ofSeconds(1);
        }
        if (!properties.getWorker().isExponentialBackoff()) {
            return retryDelay;
        }
        long multiplier = 1L << Math.min(Math.max(0, job.getAttempts() - 1), 10);
        return retryDelay.multipliedBy(multiplier);
    }

    private void waitForWork() {
        Duration idleInterval = properties.getWorker().getIdleInterval();
        long millis = idleInterval == null ? 5000 : Math.max(100, idleInterval.toMillis());
        synchronized (signal) {
            try {
                signal.wait(millis);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private String workerId() {
        return UUID.randomUUID().toString();
    }

    private ThreadFactory threadFactory() {
        return task -> {
            Thread thread = new Thread(task);
            thread.setName("postgresql-job-worker-" + thread.threadId());
            thread.setDaemon(true);
            return thread;
        };
    }
}
