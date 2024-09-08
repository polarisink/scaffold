package com.scaffold.web.config;

import com.scaffold.redis.utils.RedisUtils;
import com.scaffold.security.config.TokenService;
import org.springframework.stereotype.Component;

/**
 * @author lqsgo
 */
@Component
public class RedisToken implements TokenService {
    private static final String TOKEN_KEY = "token:";

    @Override
    public void set(Long userId, String token) {
        RedisUtils.set(TOKEN_KEY + userId, token);
    }

    @Override
    public String get(Long userId) {
        return RedisUtils.get(TOKEN_KEY + userId);
    }

    @Override
    public boolean has(Long userId) {
        return RedisUtils.hasKey(TOKEN_KEY + userId);
    }

    @Override
    public void del(Long userId) {
        RedisUtils.del(TOKEN_KEY + userId);
    }
}
