package github.polarisink.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import github.polarisink.dao.bean.properties.DoubleCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @program: double-cache
 * @author: Hydra
 * @create: 2022-03-21 09:17
 **/
@Component
@RequiredArgsConstructor
public class DoubleCacheManager implements CacheManager {
  private final RedisTemplate<Object, Object> redisTemplate;
  private final DoubleCacheProperties dcConfig;
  Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

  @Override
  public Cache getCache(String name) {
    Cache cache = cacheMap.get(name);
    if (Objects.nonNull(cache)) {
      return cache;
    }

    cache = new DoubleCache(name, redisTemplate, createCaffeineCache(), dcConfig);
    Cache oldCache = cacheMap.putIfAbsent(name, cache);
    return oldCache == null ? cache : oldCache;
  }

  @Override
  public Collection<String> getCacheNames() {
    return cacheMap.keySet();
  }

  private com.github.benmanes.caffeine.cache.Cache createCaffeineCache() {
    Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder();
    Optional<DoubleCacheProperties> dcConfigOpt = Optional.ofNullable(this.dcConfig);
    dcConfigOpt.map(DoubleCacheProperties::getInit).ifPresent(caffeineBuilder::initialCapacity);
    dcConfigOpt.map(DoubleCacheProperties::getMax).ifPresent(caffeineBuilder::maximumSize);
    dcConfigOpt.map(DoubleCacheProperties::getExpireAfterWrite).ifPresent(eaw -> caffeineBuilder.expireAfterWrite(eaw, TimeUnit.SECONDS));
    dcConfigOpt.map(DoubleCacheProperties::getExpireAfterAccess).ifPresent(eaa -> caffeineBuilder.expireAfterAccess(eaa, TimeUnit.SECONDS));
    dcConfigOpt.map(DoubleCacheProperties::getRefreshAfterWrite).ifPresent(raw -> caffeineBuilder.refreshAfterWrite(raw, TimeUnit.SECONDS));
    return caffeineBuilder.build();
  }
}
