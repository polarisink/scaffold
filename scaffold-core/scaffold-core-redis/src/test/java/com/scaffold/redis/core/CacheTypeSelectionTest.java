package com.scaffold.redis.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CacheTypeSelectionTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class))
            .withUserConfiguration(CacheConfig.class);

    @Test
    void selectsCaffeineFromSpringCacheType() {
        contextRunner
                .withPropertyValues("spring.cache.type=caffeine")
                .run(context -> {
                    assertThat(context).hasSingleBean(CacheManager.class);
                    assertThat(context.getBean(CacheManager.class)).isInstanceOf(CaffeineCacheManager.class);
                });
    }

    @Test
    void selectsRedisFromSpringCacheType() {
        contextRunner
                .withPropertyValues("spring.cache.type=redis")
                .withBean(RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(CacheManager.class);
                    assertThat(context.getBean(CacheManager.class)).isInstanceOf(RedisCacheManager.class);
                });
    }
}
