package com.scaffold.postgresql;

import com.scaffold.cache.ScaffoldCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时清理 PostgreSQL 过期缓存。
 */
@Slf4j
@RequiredArgsConstructor
public class PostgresqlCacheCleaner {

    private final PostgresqlCacheStore cacheStore;
    private final TaskScheduler taskScheduler;
    private final ScaffoldCacheProperties.Postgresql properties;

    private ScheduledFuture<?> scheduledFuture;

    public void start() {
        if (!properties.isScheduledCleanup()) {
            return;
        }
        Duration interval = properties.getCleanupInterval();
        if (interval == null || interval.isZero() || interval.isNegative()) {
            return;
        }
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(this::cleanExpired, Instant.now().plus(interval), interval);
    }

    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    private void cleanExpired() {
        String tableName = cacheStore.validateTableName(properties.getTableName());
        int count = cacheStore.evictExpired(tableName);
        if (count > 0) {
            log.debug("cleaned {} expired postgresql cache rows", count);
        }
    }

}
