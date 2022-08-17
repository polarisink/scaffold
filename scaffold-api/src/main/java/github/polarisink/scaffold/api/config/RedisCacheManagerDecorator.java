package github.polarisink.scaffold.api.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static github.polarisink.scaffold.common.constant.RedisConst.REDIS_SEP;


/**
 * @author aries
 * @date 2022/5/5
 * 这是一个CacheManager装饰器,本质上包装了RedisCache
 * 使用装饰器模式扩展RedisCache的功能
 * 扩展的功能有：
 * *. evict方法支持删除以'*'结尾的通配符表示的所有的key(若key的字符串结尾为'*',则删除这个通配符匹配下的所有的key);
 */
public class RedisCacheManagerDecorator {
  private static final ConcurrentMap<Cache, Cache> CACHE_MAP = new ConcurrentHashMap<>();
  private static final List<MethodHandler> METHOD_HANDLERS = new ArrayList<>();

  public static final String REDIS_CLEAR_ALL = "*";

  static {
    loadMethodHandlers();
  }

  private final CacheManager baseManager;

  private RedisCacheManagerDecorator(CacheManager baseManager) {
    this.baseManager = baseManager;
  }

  /**
   * 装饰CacheManager
   *
   * @param manager 要被装饰的CacheManager
   * @return 装饰后的代理对象
   */
  public static CacheManager decorate(CacheManager manager) {
    RedisCacheManagerDecorator decorator = new RedisCacheManagerDecorator(manager);
    return (CacheManager) Proxy.newProxyInstance(manager.getClass().getClassLoader(), AbstractCacheManager.class.getInterfaces(), cacheManagerInvocationHandler(decorator));
  }

  private static InvocationHandler cacheManagerInvocationHandler(RedisCacheManagerDecorator decorator) {
    return (proxy, method, args) -> {
      Object res = method.invoke(decorator.baseManager, args);
      if (method.getName().equals("getCache")) {
        res = decorateCache(res);
      }
      return res;
    };
  }

  private static Object decorateCache(Object cacheObj) {
    if (cacheObj instanceof Cache) {
      Cache cache = (Cache) cacheObj;
      Cache wrapperCache = CACHE_MAP.get(cache);
      if (wrapperCache == null) {
        wrapperCache = buildRedisCacheProxy(cache);
        CACHE_MAP.putIfAbsent(cache, wrapperCache);
      }
      return wrapperCache;
    }
    return cacheObj;
  }

  /**
   * 代理RedisCache
   */
  private static Cache buildRedisCacheProxy(Cache cache) {
    InvocationHandler handler = (proxy, method, args) -> {
      MethodHandler methodHandler = METHOD_HANDLERS.stream().filter(h -> h.canHandle(cache, method, args)).findFirst().orElse(null);
      if (methodHandler != null) {
        return methodHandler.handle(cache, method, args);
      }
      return method.invoke(cache, args);
    };
    return (Cache) Proxy.newProxyInstance(cache.getClass().getClassLoader(), new Class<?>[]{Cache.class}, handler);
  }

  private static void loadMethodHandlers() {
    METHOD_HANDLERS.add(new EvictMethodHandler());
  }

  interface MethodHandler {

    boolean canHandle(Cache cache, Method method, Object[] args);

    Object handle(Cache cache, Method method, Object[] args);
  }

  /**
   * 代理evict方法
   * 若key以'*'结尾,删除满足这个key的pattern的所有key
   */
  static class EvictMethodHandler implements MethodHandler {
    private static Method targetMethod;

    static {
      try {
        targetMethod = Cache.class.getMethod("evict", Object.class);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }

    String parsePattern(Object[] args) {
      if (args == null || args.length == 0) {
        return null;
      }
      String value = String.valueOf(args[0]);
      if (value.endsWith(REDIS_CLEAR_ALL)) {
        return value;
      }
      return null;
    }

    @Override
    public boolean canHandle(Cache cache, Method method, Object[] args) {
      return cache instanceof RedisCache && method.equals(targetMethod) && parsePattern(args) != null;
    }

    @Override
    public Object handle(Cache cache, Method method, Object[] args) {
      RedisCache redisCache = (RedisCache) cache;
      String pattern = parsePattern(args);
      RedisCacheConfiguration configuration = redisCache.getCacheConfiguration();
      pattern = redisCache.getName() + REDIS_SEP + pattern;
      RedisCacheWriter cacheWriter = redisCache.getNativeCache();
      byte[] convert = configuration.getConversionService().convert(pattern, byte[].class);
      if (convert != null) {
        cacheWriter.clean(redisCache.getName(), convert);
      }
      return null;
    }
  }
}
