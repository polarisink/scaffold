package com.scaffold.rbac.components;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.scaffold.security.config.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.concurrent.TimeUnit;

import static com.scaffold.security.config.TokenService.tokenPrefix;

@Slf4j
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
public class CaffeineToken implements TokenService {
    private final Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES) // 默认过期时间
            .removalListener((key, value, cause) -> {
                // 自定义过期处理逻辑（如记录日志）
                log.info("Token {} 已过期，原因：{}", key, cause);
            })
            .build();

    @Override
    public void set(Long userId, String token) {
        tokenCache.put(tokenPrefix(userId), token);
    }

    @Override
    public String get(Long userId) {
        return tokenCache.getIfPresent(tokenPrefix(userId));
    }

    @Override
    public boolean has(Long userId) {
        return tokenCache.getIfPresent(tokenPrefix(userId)) != null;
    }

    @Override
    public void del(Long userId) {
        tokenCache.invalidate(tokenPrefix(userId));
    }

}
