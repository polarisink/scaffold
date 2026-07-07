package com.scaffold.redis.core;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's cache abstraction without selecting a cache provider.
 * The provider is selected by {@code spring.cache.type}.
 */
@EnableCaching
@Configuration
public class CacheConfig {
}
