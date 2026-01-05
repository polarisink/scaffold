package com.scaffold.base.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GenericCaffeineFactory {
    private static final Map<Class<?>, Cache<?, ?>> caches = new ConcurrentHashMap<>();

    public static <K, V> Cache<K, V> getCache(Class<V> clazz) {
        return (Cache<K, V>) caches.computeIfAbsent(clazz, k ->
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .build());
    }
}
