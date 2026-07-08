package com.scaffold.redis.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.base.config.JacksonConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    @Test
    void redisCacheUsesJsonValueSerialization() {
        contextRunner
                .withBean("redisObjectMapper", ObjectMapper.class, () -> new JacksonConfig().redisObjectMapper())
                .run(context -> {
                    RedisCacheManager.RedisCacheManagerBuilder builder =
                            RedisCacheManager.builder(mock(RedisConnectionFactory.class));
                    context.getBean(RedisCacheManagerBuilderCustomizer.class).customize(builder);

                    RedisCacheConfiguration configuration = builder.cacheDefaults();

                    assertThatCode(() -> configuration.getValueSerializationPair()
                            .write(List.of(new NonSerializableValue("org"))))
                            .doesNotThrowAnyException();
                });
    }

    @Test
    void evictsStaleEntryWhenRedisValueDeserializationFails() {
        contextRunner.run(context -> {
            CacheErrorHandler errorHandler = context.getBean(CacheErrorHandler.class);
            Cache cache = mock(Cache.class);

            errorHandler.handleCacheGetError(new SerializationException("bad cache value"), cache, "menu-key");

            verify(cache).evictIfPresent("menu-key");
        });
    }

    @Test
    void stillPropagatesNonSerializationCacheGetErrors() {
        contextRunner.run(context -> {
            CacheErrorHandler errorHandler = context.getBean(CacheErrorHandler.class);
            Cache cache = mock(Cache.class);
            RuntimeException exception = new IllegalStateException("redis unavailable");

            assertThatThrownBy(() -> errorHandler.handleCacheGetError(exception, cache, "menu-key"))
                    .isSameAs(exception);
        });
    }

    public record NonSerializableValue(String name) {
    }
}
