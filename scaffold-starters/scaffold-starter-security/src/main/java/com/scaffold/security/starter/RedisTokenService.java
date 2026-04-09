package com.scaffold.security.starter;

import com.scaffold.redis.utils.RedisUtils;
import com.scaffold.security.config.TokenService;

import java.util.concurrent.TimeUnit;

public class RedisTokenService implements TokenService {

    private final long ttlMinutes;

    public RedisTokenService(long ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void set(Long userId, String token) {
        RedisUtils.set(TokenService.tokenPrefix(userId), token, ttlMinutes, TimeUnit.MINUTES);
    }

    @Override
    public String get(Long userId) {
        return RedisUtils.get(TokenService.tokenPrefix(userId));
    }

    @Override
    public boolean has(Long userId) {
        return RedisUtils.hasKey(TokenService.tokenPrefix(userId));
    }

    @Override
    public void del(Long userId) {
        RedisUtils.del(TokenService.tokenPrefix(userId));
    }
}
