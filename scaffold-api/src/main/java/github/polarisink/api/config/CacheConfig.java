//package github.polarisink.api.config;
//
//import github.polarisink.cache.config.DoubleCacheManager;
//import github.polarisink.dao.properties.DoubleCacheProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//
///**
// * @program: double-cache
// * @author: Hydra
// * @create: 2022-03-25 15:01
// **/
//@Configuration
//public class CacheConfig {
//
//  @Bean
//  public DoubleCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate, DoubleCacheProperties doubleCacheProperties) {
//    return new DoubleCacheManager(redisTemplate, doubleCacheProperties);
//  }
//}
