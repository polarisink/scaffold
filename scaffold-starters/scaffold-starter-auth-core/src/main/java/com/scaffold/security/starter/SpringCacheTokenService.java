package com.scaffold.security.starter;

import com.scaffold.security.config.TokenService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;

public class SpringCacheTokenService implements TokenService {

    private final CacheManager cacheManager;

    public SpringCacheTokenService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    @CachePut(cacheNames = TOKEN_CACHE_NAME, key = "#root.target.tokenPrefix(#userId)")
    public String set(String userId, String token) {
        return token;
    }

    @Override
    @Cacheable(cacheNames = TOKEN_CACHE_NAME, key = "#root.target.tokenPrefix(#userId)", unless = "#result == null")
    public String get(String userId) {
        return null;
    }

    @Override
    public boolean has(String userId) {
        Cache cache = cacheManager.getCache(TOKEN_CACHE_NAME);
        Assert.notNull(cache, "Token cache not found: " + TOKEN_CACHE_NAME);
        return cache.get(tokenPrefix(userId)) != null;
    }

    @Override
    @CacheEvict(cacheNames = TOKEN_CACHE_NAME, key = "#root.target.tokenPrefix(#userId)")
    public void del(String userId) {
    }
}
