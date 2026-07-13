package com.scaffold.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class TwoLevelCacheManager implements CacheManager {

    private final CacheManager first;
    private final CacheManager second;
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    public TwoLevelCacheManager(CacheManager first, CacheManager second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, key -> {
            Cache firstCache = first.getCache(key);
            Cache secondCache = second.getCache(key);
            if (firstCache == null || secondCache == null) return null;
            return new TwoLevelCache(firstCache, secondCache);
        });
    }

    @Override
    public Collection<String> getCacheNames() {
        Set<String> names = new LinkedHashSet<>(first.getCacheNames());
        names.addAll(second.getCacheNames());
        names.addAll(caches.keySet());
        return names;
    }
}
