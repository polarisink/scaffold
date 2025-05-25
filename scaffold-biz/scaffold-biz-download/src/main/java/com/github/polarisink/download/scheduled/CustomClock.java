package com.github.polarisink.download.scheduled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class CustomClock extends Clock {

    public final AtomicBoolean isPaused = new AtomicBoolean(false);  // 暂停标志
    @Getter
    private final AtomicLong virtualTimeMillis;  // 虚拟时间（毫秒）
    private final AtomicLong lastUpdateTime;  // 上一次更新时间的时间戳
    @Getter
    private int step = 1;  // 时间流逝速率，1 表示正常，2 表示加速等

    private CustomClock(LocalDateTime time) {
        this.virtualTimeMillis = new AtomicLong(time.toInstant(ZoneOffset.UTC).toEpochMilli());  // 初始化虚拟时间
        this.lastUpdateTime = new AtomicLong(System.currentTimeMillis());
    }

    public static CustomClock create(LocalDateTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Initial time cannot be null.");
        }
        return new CustomClock(time);
    }

    private static CustomClock create() {
        return new CustomClock(LocalDateTime.now());
    }

    public static void main(String[] args) throws InterruptedException {
        CustomClock customClock = CustomClock.create();
        customClock.pause();
        log.info("current clock: {}, system time: {}", customClock.instant(), LocalDateTime.now());
        TimeUnit.SECONDS.sleep(3);
        log.info("current clock: {}, system time: {}", customClock.instant(), LocalDateTime.now());
    }

    /**
     * 回滚虚拟时间到指定的时间点
     *
     * @param time 目标时间
     */
    public void rollback(LocalDateTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Target time cannot be null.");
        }
        long targetMillis = time.toInstant(ZoneOffset.UTC).toEpochMilli();
        if (!virtualTimeMillis.compareAndSet(virtualTimeMillis.get(), targetMillis)) {
            log.warn("Failed to rollback virtual time due to concurrent modification.");
        } else {
            this.lastUpdateTime.set(System.currentTimeMillis());
            log.info("Virtual time rolled back to: {}", time);
        }
    }

    @Override
    public Instant instant() {
        if (isPaused.get()) {
            // 如果暂停，返回当前的虚拟时间，不再变化
            return Instant.ofEpochMilli(virtualTimeMillis.get());
        }

        long currentTimeMillis = System.currentTimeMillis();
        long lastUpdate = lastUpdateTime.get();
        long elapsedMillis = currentTimeMillis - lastUpdate;

        // 检测系统时间是否被调整
        if (elapsedMillis < 0) {
            log.warn("System clock has been adjusted backward. Elapsed time: {} ms", elapsedMillis);
            elapsedMillis = 0; // 防止负值影响计算
        }

        if (elapsedMillis > 0) { // 防止负值
            long updatedVirtualTime = virtualTimeMillis.addAndGet(elapsedMillis * step);

            // 减少原子操作次数，仅在必要时更新 lastUpdateTime
            if (updatedVirtualTime != virtualTimeMillis.get()) {
                lastUpdateTime.set(currentTimeMillis); // 更新上一次更新时间
            }

            return Instant.ofEpochMilli(updatedVirtualTime);
        }
        return Instant.ofEpochMilli(virtualTimeMillis.get());
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return Clock.system(zone);
    }

    /**
     * 设置时间加速/减速的速率
     *
     * @param timeSpeed 加速倍率，必须大于 0
     * @throws IllegalArgumentException 如果 timeSpeed 小于等于 0
     */
    public void step(int timeSpeed) {
        if (timeSpeed <= 0) {
            throw new IllegalArgumentException("Speed must be greater than 0.");
        }
        this.step = timeSpeed;
        log.info("Time speed set to: {}", timeSpeed);
    }

    /**
     * 暂停虚拟时间流逝
     */
    public void pause() {
        if (!isPaused.compareAndSet(false, true)) {
            log.warn("Custom clock is already paused.");
        } else {
            log.info("Custom clock paused.");
        }
    }

    /**
     * 恢复虚拟时间流逝
     */
    public void resume() {
        if (!isPaused.compareAndSet(true, false)) {
            log.warn("Custom clock is already running.");
        } else {
            log.info("Custom clock resumed.");
        }
    }
}
