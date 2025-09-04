package com.github.polarisink.download.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class TrainEngineService {

    private static final Map<String, CustomThreadPoolTaskScheduler> taskSchedulerMap = new ConcurrentHashMap<>();

    private static CustomThreadPoolTaskScheduler threadPoolTaskScheduler(CustomClock clock) {
        CustomThreadPoolTaskScheduler scheduler = new CustomThreadPoolTaskScheduler();
        scheduler.setPoolSize(100);
        scheduler.setClock(clock);
        scheduler.setThreadNamePrefix("scheduled");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.setErrorHandler(TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER);
        return scheduler;
    }

    // Schedule task
    /*public ScheduledFuture<?> schedule(String trainId, Runnable runnable, Duration delay) {
        return getScheduler(trainId).scheduleWithFixedDelay(runnable, delay);
    }*/

    // Release resources
    public void release(String trainId) {
        Optional.ofNullable(taskSchedulerMap.remove(trainId)).ifPresent(ThreadPoolTaskScheduler::destroy);
    }

    // Schedule task with dynamic delay based on step
    public void schedule(String trainId, Runnable runnable, Duration initialDelay) {
        CustomThreadPoolTaskScheduler scheduler = getScheduler(trainId);
        int timeStep = getClock(trainId).getStep();

        // Initial delay calculation based on step
        long adjustedDelayMillis = initialDelay.toMillis() * timeStep;

        // Define a task that reschedules itself after execution
        Runnable wrappedTask = new Runnable() {
            @Override
            public void run() {
                // Run the task
                runnable.run();

                // Calculate the next delay based on the current time step
                long nextAdjustedDelayMillis = initialDelay.toMillis() * getClock(trainId).getStep();

                // Reschedule the task with the new delay
                scheduler.scheduleWithFixedDelay(this, Duration.ofMillis(nextAdjustedDelayMillis));
            }
        };

        // Initially schedule the task with the adjusted delay
        scheduler.scheduleWithFixedDelay(wrappedTask, Duration.ofMillis(adjustedDelayMillis));
    }

    // Pause clock
    public void pause(String trainId) {
        getClock(trainId).pause();
    }

    public void resume(String trainId) {
        getClock(trainId).resume();
    }

    // Adjust time speed
    public void step(String trainId, int step) {
        getClock(trainId).step(step);
    }

    // Rollback time
    public void rollback(String trainId, Integer timeOffsetSeconds) {
        CustomClock clock = getClock(trainId);
    }

    // Get CustomClock instance for a trainId
    private CustomClock getClock(String trainId) {
        return getScheduler(trainId).getClock();
    }

    // Get task scheduler for a trainId
    private CustomThreadPoolTaskScheduler getScheduler(String trainId) {
        return Optional.ofNullable(taskSchedulerMap.get(trainId)).orElseThrow();
    }

    // Initialize scheduler with a specific time
    public void initScheduler(String trainId, LocalDateTime time) {
        taskSchedulerMap.computeIfAbsent(trainId, t -> {
            CustomClock customClock = CustomClock.create(time);
            CustomThreadPoolTaskScheduler scheduler = threadPoolTaskScheduler(customClock);
            scheduler.initialize();
            return scheduler;
        });
    }
}


