package com.scaffold.postgresql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 基于 PostgreSQL 表的 Spring Cache 实现。
 */
@Slf4j
public class PostgresqlCache extends AbstractValueAdaptingCache {

    private final String name;
    private final PostgresqlCacheStore cacheStore;
    private final String tableName;
    private final Duration defaultTtl;
    private final SerializingConverter serializer = new SerializingConverter();
    private final DeserializingConverter deserializer = new DeserializingConverter();

    public PostgresqlCache(String name,
                           PostgresqlCacheStore cacheStore,
                           String tableName,
                           Duration defaultTtl,
                           boolean allowNullValues) {
        super(allowNullValues);
        this.name = name;
        this.cacheStore = cacheStore;
        this.tableName = tableName;
        this.defaultTtl = defaultTtl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return cacheStore;
    }

    @Override
    @Nullable
    protected Object lookup(Object key) {
        String cacheKey = convertKey(key);
        List<byte[]> values = cacheStore.findValue(tableName, name, cacheKey);
        if (values.isEmpty()) {
            evictIfExpired(cacheKey);
            return null;
        }
        return deserialize(values.getFirst());
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        cacheStore.put(tableName, name, convertKey(key), serialize(toStoreValue(value)), expiresAt());
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
        String cacheKey = convertKey(key);
        evictIfExpired(cacheKey);
        byte[] cacheValue = serialize(toStoreValue(value));
        int updated = cacheStore.putIfAbsent(tableName, name, cacheKey, cacheValue, expiresAt());
        if (updated > 0) {
            return null;
        }
        return get(key);
    }

    @Override
    public void evict(Object key) {
        cacheStore.evict(tableName, name, convertKey(key));
    }

    @Override
    public void clear() {
        cacheStore.clear(tableName, name);
    }

    void evictExpired() {
        int count = cacheStore.evictExpired(tableName);
        if (count > 0) {
            log.debug("cleaned {} expired postgresql cache rows", count);
        }
    }

    private void evictIfExpired(String cacheKey) {
        cacheStore.evictExpired(tableName, name, cacheKey);
    }

    private String convertKey(Object key) {
        return String.valueOf(key);
    }

    @Nullable
    private Timestamp expiresAt() {
        if (defaultTtl == null || defaultTtl.isZero() || defaultTtl.isNegative()) {
            return null;
        }
        return Timestamp.from(Instant.now().plus(defaultTtl));
    }

    private byte[] serialize(Object value) {
        return serializer.convert(value);
    }

    private Object deserialize(byte[] bytes) {
        return deserializer.convert(bytes);
    }
}
