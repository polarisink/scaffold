package com.scaffold.postgresql.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态创建 Caffeine Cache 的 CacheManager。
 */
public class ScaffoldCaffeineCacheManager implements CacheManager {

    private final ScaffoldCacheProperties.Caffeine properties;
    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    public ScaffoldCaffeineCacheManager(ScaffoldCacheProperties.Caffeine properties) {
        this.properties = properties;
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
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(properties.getMaximumSize());
        Duration expireAfterWrite = properties.getExpireAfterWrite();
        if (expireAfterWrite != null && !expireAfterWrite.isZero() && !expireAfterWrite.isNegative()) {
            builder.expireAfterWrite(expireAfterWrite);
        }
        return new CaffeineCache(name, builder.build(), true);
    }
}
