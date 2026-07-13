package com.scaffold.security.starter;

import com.scaffold.security.config.TokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;

@RequiredArgsConstructor
public class SpringCacheTokenStore implements TokenStore {

    private final CacheManager cacheManager;

    @Override
    @CachePut(cacheNames = TOKEN_CACHE_NAME, key = "#userId")
    public String set(String userId, String token) {
        return token;
    }

    @Override
    @Cacheable(cacheNames = TOKEN_CACHE_NAME, key = "#userId", unless = "#result == null")
    public String get(String userId) {
        return null;
    }

    @Override
    public boolean has(String userId) {
        Cache cache = cacheManager.getCache(TOKEN_CACHE_NAME);
        Assert.notNull(cache, "Token cache not found: " + TOKEN_CACHE_NAME);
        return cache.get(userId) != null;
    }

    @Override
    @CacheEvict(cacheNames = TOKEN_CACHE_NAME, key = "#userId")
    public void del(String userId) {
    }
}
