package github.polarisink.api.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static github.polarisink.common.constant.AuthConst.LONG_TIME;
import static github.polarisink.common.constant.AuthConst.VERIFY_CODE_TIMEOUT;


/**
 * redis 配置信息,加入缓存
 *
 * @author wangbo
 */
@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * redis缓存分隔符
     */
    public static final String REDIS_SEP = StrUtil.COLON;
    public static final String DOLLAR = "$";
    public static final String SHARP = "#";
    private final ApplicationContext applicationContext;
    private final RedisConnectionFactory factory;

    /**
     * 处理时间间隔字符串
     *
     * @param s
     * @return
     */
    private static Duration parseDuration(String s) {
        int time = Integer.parseInt(s.substring(0, s.length() - 1));
        char c = s.charAt(s.length() - 1);
        switch (c) {
            case 's':
                return Duration.ofSeconds(time);
            case 'm':
                return Duration.ofMinutes(time);
            case 'd':
                return Duration.ofDays(time);
            default:
                //默认存储3天
                return Duration.ofDays(3);
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<Object> serializer = redisSerializer();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate2(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<Object> serializer = redisSerializer();
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        //set for localDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        //必须设置，否则无法将JSON转化为对象，会转化成Map类型
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    @Bean
    @Primary
    public CacheManager redisCacheManager() {
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(factory);
        GenericJackson2JsonRedisSerializer jackson = new GenericJackson2JsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(
                jackson);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair)
                .entryTtl(Duration.ofSeconds(30));
        Map<String, RedisCacheConfiguration> conf = getConf();
        LOG.info("dynamicTimeoutConf: {}", conf);
        return new RedisCacheManager(writer, config, conf);
    }

    /**
     * 验证码缓存管理器
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean("verifyCodeCacheManager")
    public CacheManager verifyCodeCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置验证码时间为30分钟
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(VERIFY_CODE_TIMEOUT))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()))
                .computePrefixWith(name -> name + ":");
        RedisCacheManager cacheManager = new RedisCacheManager(writer, config);
        return RedisCacheManagerDecorator.decorate(cacheManager);
    }

    /**
     * 长时间存数据,1000天
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean("longTime")
    public CacheManager longTime(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置Redis缓存有效期为1天
        //短信验证码两套之内过期
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(LONG_TIME))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer()))
                .computePrefixWith(name -> name + ":");
        RedisCacheManager cacheManager = new RedisCacheManager(writer, config);
        return RedisCacheManagerDecorator.decorate(cacheManager);
    }

    /**
     * errorHandler
     *
     * @return
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
        LOG.info("初始化 -> [{}]", "Redis CacheErrorHandler");
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                LOG.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                LOG.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                LOG.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                LOG.error("Redis occur handleCacheClearError：", e);
            }
        };
        return cacheErrorHandler;
    }

    private Map<String, RedisCacheConfiguration> getConf() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Component.class);
        List<CacheMap> collect = beansWithAnnotation.entrySet().stream().flatMap(entry -> {
            try {
                Object value = entry.getValue();
                // 获得原本的class名字，spring代理的都是后面有$$直接截取即可
                String name = value.getClass().getName();
                String className = name.contains(DOLLAR) ? name.substring(0, name.indexOf(DOLLAR)) : name;
                // 获得原始的字节码文件，如果被spring 代理之后，方法上会获取不到注解信息
                Method[] methods = Class.forName(className).getDeclaredMethods();
                return Arrays.stream(methods).flatMap(method -> {
                    Cacheable annotation = method.getAnnotation(Cacheable.class);
                    return Objects.isNull(annotation) ? null : Arrays.stream(annotation.cacheNames()).map(data -> {
                        if (!data.contains(SHARP)) {
                            return null;
                        }
                        // 包含自定义日期
                        String[] split = data.split(SHARP);
                        //#将key分为两部分分为两部分
                        if (split.length != 2) {
                            return null;
                        }
                        CacheMap cacheMap = new CacheMap();
                        String s = split[1];
                        cacheMap.setName(data);
                        cacheMap.setTtl(parseDuration(s));
                        return cacheMap;
                    }).filter(Objects::nonNull);
                });
            } catch (Exception e) {
                LOG.info("异常");
                return null;
            }
        }).collect(Collectors.toList());
        return collect.stream().collect(Collectors.toMap(CacheMap::getName, p -> {
            GenericJackson2JsonRedisSerializer jackson = new GenericJackson2JsonRedisSerializer();
            RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(
                    jackson);
            return RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair).entryTtl(p.getTtl());
        }, (key1, key2) -> key2));
    }

    @Data
    static class CacheMap {

        private String name;
        private Duration ttl;
    }
}



  /*
  @Primary
  @Bean("cacheManager")
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
    //设置Redis缓存有效期为3天
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer())).computePrefixWith(name -> name + ":");
    RedisCacheManager cacheManager = new RedisCacheManager(writer, config);
    return RedisCacheManagerDecorator.decorate(cacheManager);
  }*/
