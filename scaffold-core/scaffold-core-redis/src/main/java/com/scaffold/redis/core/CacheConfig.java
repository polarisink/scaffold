package com.scaffold.redis.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Enables Spring's cache abstraction without selecting a cache provider.
 * The provider is selected by {@code spring.cache.type}.
 */
@EnableCaching
@Configuration
public class CacheConfig {

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    @ConditionalOnClass(RedisCacheManager.class)
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(CacheProperties cacheProperties,
            @Qualifier("redisObjectMapper") ObjectProvider<ObjectMapper> redisObjectMapperProvider,
            ObjectProvider<ObjectMapper> objectMapperProvider) {
        ObjectMapper objectMapper = redisObjectMapperProvider.getIfAvailable(
                () -> objectMapperProvider.getIfAvailable(ObjectMapper::new)
        );
        return builder -> {
            builder.cacheDefaults(redisCacheConfiguration(cacheProperties, objectMapper));
            if (cacheProperties.getRedis().isEnableStatistics()) {
                builder.enableStatistics();
            }
        };
    }

    private RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties, ObjectMapper objectMapper) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(objectMapper)
                ));

        CacheProperties.Redis redis = cacheProperties.getRedis();
        Duration timeToLive = redis.getTimeToLive();
        if (timeToLive != null) {
            configuration = configuration.entryTtl(timeToLive);
        }
        if (!redis.isCacheNullValues()) {
            configuration = configuration.disableCachingNullValues();
        }
        if (redis.getKeyPrefix() != null) {
            configuration = configuration.prefixCacheNameWith(redis.getKeyPrefix());
        }
        if (!redis.isUseKeyPrefix()) {
            configuration = configuration.disableKeyPrefix();
        }
        return configuration;
    }

    @Bean
    @ConditionalOnClass(RedisCacheManager.class)
    @ConditionalOnMissingBean(CacheErrorHandler.class)
    public CacheErrorHandler redisCacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                if (isSerializationError(exception)) {
                    log.warn("Redis cache value deserialize failed, evict stale entry. cache={}, key={}",
                            cache.getName(), key);
                    cache.evictIfPresent(key);
                    return;
                }
                super.handleCacheGetError(exception, cache, key);
            }
        };
    }

    private boolean isSerializationError(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof SerializationException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
