package com.scaffold.postgresql.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态创建 PostgreSQL Cache 的 CacheManager。
 */
public class PostgresqlCacheManager implements CacheManager {

    private final PostgresqlCacheStore cacheStore;
    private final ScaffoldCacheProperties.Postgresql properties;
    private final String tableName;
    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    public PostgresqlCacheManager(PostgresqlCacheStore cacheStore, ScaffoldCacheProperties.Postgresql properties) {
        this.cacheStore = cacheStore;
        this.properties = properties;
        this.tableName = cacheStore.validateTableName(properties.getTableName());
        initializeSchema();
        cleanupOnStartup();
    }

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, this::createCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(caches.keySet());
    }

    private Cache createCache(String name) {
        Duration ttl = properties.getDefaultTtl();
        return new PostgresqlCache(name, cacheStore, tableName, ttl, true);
    }

    private void initializeSchema() {
        if (!properties.isInitializeSchema()) {
            return;
        }
        cacheStore.initializeSchema(tableName, properties.isUnlogged());
    }

    private void cleanupOnStartup() {
        if (!properties.isCleanupOnStartup()) {
            return;
        }
        cacheStore.evictExpired(tableName);
    }
}
