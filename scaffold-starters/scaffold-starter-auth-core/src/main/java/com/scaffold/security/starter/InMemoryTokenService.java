package com.scaffold.security.starter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.scaffold.security.config.TokenService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemoryTokenService implements TokenService {

    private final Cache<String, String> tokenCache;

    public InMemoryTokenService(long ttlMinutes) {
        this.tokenCache = Caffeine.newBuilder()
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> log.info("Token {} removed, cause: {}", key, cause))
                .build();
    }

    @Override
    public void set(String userId, String token) {
        tokenCache.put(tokenPrefix(userId), token);
    }

    @Override
    public String get(String userId) {
        return tokenCache.getIfPresent(tokenPrefix(userId));
    }

    @Override
    public boolean has(String userId) {
        return tokenCache.getIfPresent(tokenPrefix(userId)) != null;
    }

    @Override
    public void del(String userId) {
        tokenCache.invalidate(tokenPrefix(userId));
    }
}
