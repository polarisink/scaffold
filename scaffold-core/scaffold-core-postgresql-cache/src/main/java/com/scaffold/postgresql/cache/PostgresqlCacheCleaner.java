package com.scaffold.postgresql.cache;

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
    private final ScaffoldCacheProperties properties;

    private ScheduledFuture<?> scheduledFuture;

    public void start() {
        ScaffoldCacheProperties.Postgresql postgresql = properties.getPostgresql();
        if (!needPostgresqlCache() || !postgresql.isScheduledCleanup()) {
            return;
        }
        Duration interval = postgresql.getCleanupInterval();
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
        String tableName = cacheStore.validateTableName(properties.getPostgresql().getTableName());
        int count = cacheStore.evictExpired(tableName);
        if (count > 0) {
            log.debug("cleaned {} expired postgresql cache rows", count);
        }
    }

    private boolean needPostgresqlCache() {
        return properties.getMode() == ScaffoldCacheProperties.CacheMode.POSTGRESQL
                || properties.getMode() == ScaffoldCacheProperties.CacheMode.TWO_LEVEL;
    }

}
