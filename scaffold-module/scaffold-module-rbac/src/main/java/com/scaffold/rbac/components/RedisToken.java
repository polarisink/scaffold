package com.scaffold.rbac.components;

import com.scaffold.redis.utils.RedisUtils;
import com.scaffold.security.config.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;

import static com.scaffold.security.config.TokenService.tokenPrefix;

/**
 * @author lqsgo
 */
@Component
@ConditionalOnBean(RedisProperties.class)
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisToken implements TokenService {

    @Override
    public void set(Long userId, String token) {
        RedisUtils.set(tokenPrefix(userId), token);
    }

    @Override
    public String get(Long userId) {
        return RedisUtils.get(tokenPrefix(userId));
    }

    @Override
    public boolean has(Long userId) {
        return RedisUtils.hasKey(tokenPrefix(userId));
    }

    @Override
    public void del(Long userId) {
        RedisUtils.del(tokenPrefix(userId));
    }
}
