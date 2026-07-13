package com.scaffold.cache;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public final class TwoLevelCache implements Cache {

    private final Cache first;
    private final Cache second;

    public TwoLevelCache(Cache first, Cache second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String getName() { return first.getName(); }

    @Override
    public Object getNativeCache() { return this; }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = first.get(key);
        if (value != null) return value;
        value = second.get(key);
        if (value != null) first.put(key, value.get());
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper value = get(key);
        return value == null ? null : type.cast(value.get());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper value = get(key);
        if (value != null) return (T) value.get();
        T loaded = second.get(key, valueLoader);
        first.put(key, loaded);
        return loaded;
    }

    @Override
    public void put(Object key, Object value) {
        second.put(key, value);
        first.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper existing = get(key);
        if (existing != null) return existing;
        put(key, value);
        return null;
    }

    @Override
    public void evict(Object key) {
        second.evict(key);
        first.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        boolean secondPresent = second.evictIfPresent(key);
        boolean firstPresent = first.evictIfPresent(key);
        return firstPresent || secondPresent;
    }

    @Override
    public void clear() {
        second.clear();
        first.clear();
    }

    @Override
    public boolean invalidate() {
        boolean secondInvalidated = second.invalidate();
        boolean firstInvalidated = first.invalidate();
        return firstInvalidated || secondInvalidated;
    }
}
