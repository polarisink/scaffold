package github.polarisink.scaffold.api.config;

import github.polarisink.scaffold.dao.properties.DoubleCacheProperties;
import github.polarisink.scaffold.cache.config.DoubleCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @program: double-cache
 * @author: Hydra
 * @create: 2022-03-25 15:01
 **/
@Configuration
@RequiredArgsConstructor
public class CacheConfig {
  private final DoubleCacheProperties doubleCacheProperties;

  @Bean
  public DoubleCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate, DoubleCacheProperties doubleCacheProperties) {
    return new DoubleCacheManager(redisTemplate, doubleCacheProperties);
  }
}
