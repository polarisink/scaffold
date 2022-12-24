package github.polarisink.cache.config;


import com.github.benmanes.caffeine.cache.Cache;
import github.polarisink.cache.msg.CacheMassage;
import github.polarisink.cache.msg.CacheMsgType;
import github.polarisink.cache.msg.MessageSourceUtil;
import github.polarisink.dao.bean.properties.DoubleCacheProperties;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @program: double-cache
 * @author: Hydra
 * @create: 2022-03-21 10:07
 **/
@Slf4j
public class DoubleCache extends AbstractValueAdaptingCache {

  private String cacheName;
  private RedisTemplate<Object, Object> redisTemplate;
  private Cache<Object, Object> caffeineCache;
  private DoubleCacheProperties doubleCacheProperties;
  private ReentrantLock lock = new ReentrantLock();


  protected DoubleCache(boolean allowNullValues) {
    super(allowNullValues);
  }

  public DoubleCache(String cacheName, RedisTemplate<Object, Object> redisTemplate, Cache<Object, Object> caffeineCache,
      DoubleCacheProperties doubleCacheProperties) {
    super(doubleCacheProperties.getAllowNull());
    this.cacheName = cacheName;
    this.redisTemplate = redisTemplate;
    this.caffeineCache = caffeineCache;
    this.doubleCacheProperties = doubleCacheProperties;
  }

  /**
   * 使用注解时不走这个方法，实际走父类的get方法
   */
  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    lock.lock();//加锁
    try {

      Object obj = lookup(key);
      if (Objects.nonNull(obj)) {
        return (T) obj;
      }
      //没有找到
      obj = valueLoader.call();
      //放入缓存
      put(key, obj);
      return (T) obj;
    } catch (Exception e) {
      LOG.error(e.getMessage());
    } finally {
      lock.unlock();
    }
    return null;
  }

  @Override
  protected Object lookup(Object key) {
    // 先从caffeine中查找
    Object obj = caffeineCache.getIfPresent(key);
    if (Objects.nonNull(obj)) {
      LOG.info("get data from caffeine");
      //不用fromStoreValue，否则返回的是null，会再查数据库
      return obj;
    }

    //再从redis中查找
    String redisKey = this.cacheName + ":" + key;
    obj = redisTemplate.opsForValue().get(redisKey);
    if (Objects.nonNull(obj)) {
      LOG.info("get data from redis");
      caffeineCache.put(key, obj);
    }
    return obj;
  }

  @Override
  public void put(Object key, Object value) {
    if (!isAllowNullValues() && Objects.isNull(value)) {
      LOG.error("the value NULL will not be cached");
      return;
    }

    //使用 toStoreValue(value) 包装，解决caffeine不能存null的问题
    //caffeineCache.put(key,value);
    caffeineCache.put(key, toStoreValue(value));

    // null对象只存在caffeine中一份就够了，不用存redis了
    if (Objects.isNull(value)) {
      return;
    }

    String redisKey = this.cacheName + ":" + key;
    Optional<Long> expireOpt = Optional.ofNullable(doubleCacheProperties).map(DoubleCacheProperties::getRedisExpire);
    if (expireOpt.isPresent()) {
      redisTemplate.opsForValue().set(redisKey, toStoreValue(value), expireOpt.get(), TimeUnit.SECONDS);
    } else {
      redisTemplate.opsForValue().set(redisKey, toStoreValue(value));
    }

    //发送信息通知其他节点更新一级缓存
    //同样，空对象不会给其他节点发送信息
    try {
      CacheMassage cacheMassage = new CacheMassage(this.cacheName, CacheMsgType.UPDATE, key, value,
          MessageSourceUtil.getMsgSource());
      redisTemplate.convertAndSend(MessageConfig.TOPIC, cacheMassage);
    } catch (UnknownHostException e) {
      LOG.error(e.getMessage());
    }
  }

  @Override
  public void evict(Object key) {
    redisTemplate.delete(this.cacheName + ":" + key);
    caffeineCache.invalidate(key);

    //发送信息通知其他节点删除一级缓存
    try {
      CacheMassage cacheMassage = new CacheMassage(this.cacheName, CacheMsgType.DELETE, key, null,
          MessageSourceUtil.getMsgSource());
      redisTemplate.convertAndSend(MessageConfig.TOPIC, cacheMassage);
    } catch (UnknownHostException e) {
      LOG.error(e.getMessage());
    }
  }

  @Override
  public void clear() {
    //如果是正式环境，避免使用keys命令
    Set<Object> keys = redisTemplate.keys(this.cacheName.concat(":*"));
    for (Object key : keys) {
      redisTemplate.delete(String.valueOf(key));
    }
    caffeineCache.invalidateAll();
  }

  @Override
  public String getName() {
    return this.cacheName;
  }

  @Override
  public Object getNativeCache() {
    return this;
  }

  // 更新一级缓存
  public void updateL1Cache(Object key, Object value) {
    caffeineCache.put(key, value);
  }

  // 删除一级缓存
  public void evictL1Cache(Object key) {
    caffeineCache.invalidate(key);
  }

}
