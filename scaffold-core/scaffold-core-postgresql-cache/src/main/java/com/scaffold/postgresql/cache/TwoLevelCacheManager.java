package com.scaffold.postgresql.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caffeine + PostgreSQL 两级缓存管理器。
 */
public class TwoLevelCacheManager implements CacheManager {

    private final CacheManager caffeineCacheManager;
    private final CacheManager postgresqlCacheManager;
    private final PostgresqlCacheInvalidationService invalidationService;
    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    public TwoLevelCacheManager(PostgresqlCacheStore cacheStore,
                                ScaffoldCacheProperties properties,
                                PostgresqlCacheInvalidationService invalidationService) {
        this.caffeineCacheManager = new ScaffoldCaffeineCacheManager(properties.getCaffeine());
        this.postgresqlCacheManager = new PostgresqlCacheManager(cacheStore, properties.getPostgresql());
        this.invalidationService = invalidationService;
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
        Cache caffeineCache = caffeineCacheManager.getCache(name);
        invalidationService.registerLocalCache(name, caffeineCache);
        return new TwoLevelCache(
                name,
                caffeineCache,
                postgresqlCacheManager.getCache(name),
                invalidationService
        );
    }
}
