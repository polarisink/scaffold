package com.scaffold.postgresql.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

/**
 * Caffeine 本地缓存 + PostgreSQL 共享缓存的两级缓存。
 */
public class TwoLevelCache extends AbstractValueAdaptingCache {

    private final String name;
    private final Cache caffeineCache;
    private final Cache postgresqlCache;
    private final PostgresqlCacheInvalidationService invalidationService;

    public TwoLevelCache(String name,
                         Cache caffeineCache,
                         Cache postgresqlCache,
                         PostgresqlCacheInvalidationService invalidationService) {
        super(true);
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.postgresqlCache = postgresqlCache;
        this.invalidationService = invalidationService;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return postgresqlCache.getNativeCache();
    }

    @Override
    @Nullable
    protected Object lookup(Object key) {
        String cacheKey = convertKey(key);
        ValueWrapper caffeineValue = caffeineCache.get(cacheKey);
        if (caffeineValue != null) {
            return toStoreValue(caffeineValue.get());
        }
        ValueWrapper postgresqlValue = postgresqlCache.get(key);
        if (postgresqlValue == null) {
            return null;
        }
        Object value = postgresqlValue.get();
        caffeineCache.put(cacheKey, value);
        return toStoreValue(value);
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        postgresqlCache.put(key, value);
        String cacheKey = convertKey(key);
        caffeineCache.put(cacheKey, value);
        invalidationService.publishEvict(name, cacheKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper valueWrapper = get(key);
        if (valueWrapper != null) {
            return (T) valueWrapper.get();
        }
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        ValueWrapper existingValue = get(key);
        if (existingValue != null) {
            return existingValue;
        }
        String cacheKey = convertKey(key);
        ValueWrapper postgresqlValue = postgresqlCache.putIfAbsent(key, value);
        if (postgresqlValue != null) {
            caffeineCache.put(cacheKey, postgresqlValue.get());
            return postgresqlValue;
        }
        caffeineCache.putIfAbsent(cacheKey, value);
        invalidationService.publishEvict(name, cacheKey);
        return null;
    }

    @Override
    public void evict(Object key) {
        String cacheKey = convertKey(key);
        caffeineCache.evict(cacheKey);
        postgresqlCache.evict(key);
        invalidationService.publishEvict(name, cacheKey);
    }

    @Override
    public void clear() {
        caffeineCache.clear();
        postgresqlCache.clear();
        invalidationService.publishClear(name);
    }

    private String convertKey(Object key) {
        return String.valueOf(key);
    }
}
