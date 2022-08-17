package polarisink.github.scaffold.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import polarisink.github.scaffold.cache.DoubleCacheManager;

/**
 * @program: double-cache
 * @author: Hydra
 * @create: 2022-03-25 15:01
 **/
@Configuration
@RequiredArgsConstructor
public class CacheConfig {
  private final DoubleCacheConfig doubleCacheConfig;

  @Bean
  public DoubleCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate, DoubleCacheConfig doubleCacheConfig) {
    return new DoubleCacheManager(redisTemplate, doubleCacheConfig);
  }
}
