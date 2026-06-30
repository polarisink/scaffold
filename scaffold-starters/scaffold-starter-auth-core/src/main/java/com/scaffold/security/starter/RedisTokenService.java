package com.scaffold.security.starter;

import com.scaffold.redis.utils.RedisUtils;
import com.scaffold.security.config.TokenService;

import java.time.Duration;

public class RedisTokenService implements TokenService{

    private final long ttlMinutes;

    public RedisTokenService(long ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void set(String userId, String token) {
        RedisUtils.set(tokenPrefix(userId), token, Duration.ofMinutes(ttlMinutes));
    }

    @Override
    public String get(String userId) {
        return RedisUtils.get(tokenPrefix(userId));
    }

    @Override
    public boolean has(String userId) {
        return RedisUtils.hasKey(tokenPrefix(userId));
    }

    @Override
    public void del(String userId) {
        RedisUtils.del(tokenPrefix(userId));
    }
}
