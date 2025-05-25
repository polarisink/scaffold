package com.github.polarisink.download.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class CustomThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

    @Override
    public CustomClock getClock() {
        return (CustomClock) super.getClock();
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Duration delay) {
        return super.scheduleWithFixedDelay(new PausedTaskWrapper(task), delay);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration period) {
        return super.scheduleAtFixedRate(new PausedTaskWrapper(task), period);
    }

    /**
     * 包装任务，检查是否暂停，如果暂停则跳过执行
     */
    private class PausedTaskWrapper implements Runnable {
        private final Runnable task;

        PausedTaskWrapper(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 如果时钟暂停，跳过任务执行
            CustomClock clock = getClock();
            if (clock.isPaused.get()) {
                log.info("Clock is paused, task will not execute.");
                return;  // 直接跳过任务执行
            }
            // 否则，正常执行任务
            log.info("clock:{}", clock.instant());
            task.run();
        }
    }
}
