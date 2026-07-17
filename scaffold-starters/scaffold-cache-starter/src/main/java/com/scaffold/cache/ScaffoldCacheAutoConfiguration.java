package com.scaffold.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.postgresql.PostgresqlCacheCleaner;
import com.scaffold.postgresql.PostgresqlCacheManager;
import com.scaffold.postgresql.PostgresqlCacheSerializer;
import com.scaffold.postgresql.PostgresqlCacheStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.time.Duration;

@EnableCaching
@AutoConfiguration(before = CacheAutoConfiguration.class)
@EnableConfigurationProperties(ScaffoldCacheProperties.class)
public class ScaffoldCacheAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ScaffoldCacheAutoConfiguration.class);

    private static final String POSTGRESQL_SELECTED = """
            (\
            '${scaffold.cache.mode:single}'.toLowerCase() == 'single' && \
            '${scaffold.cache.type:caffeine}'.toLowerCase() == 'postgresql') || (\
            '${scaffold.cache.mode:single}'.toLowerCase() == 'two-level' && \
            '${scaffold.cache.secondary:redis}'.toLowerCase() == 'postgresql')""";

    @Bean("caffeineCacheManager")
    @ConditionalOnMissingBean(name = "caffeineCacheManager")
    public CaffeineCacheManager caffeineCacheManager(ScaffoldCacheProperties properties) {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCacheSpecification(properties.caffeine().getSpec());
        return manager;
    }

    @Bean("redisObjectMapper")
    @ConditionalOnMissingBean(name = "redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        return JsonUtil.createRedisObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean(CacheErrorHandler.class)
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                if (isSerializationError(exception)) {
                    log.warn("Cache value deserialize failed, evict stale entry. cache={}, key={}",
                            cache.getName(), key);
                    cache.evictIfPresent(key);
                    return;
                }
                super.handleCacheGetError(exception, cache, key);
            }
        };
    }

    @Bean("redisCacheManager")
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnMissingBean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory,
                                               ScaffoldCacheProperties properties) {
        ScaffoldCacheProperties.Redis redis = properties.redis();
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        Duration ttl = redis.getTimeToLive();
        if (ttl != null && !ttl.isNegative() && !ttl.isZero()) {
            configuration = configuration.entryTtl(ttl);
        }
        if (!redis.isCacheNullValues()) {
            configuration = configuration.disableCachingNullValues();
        }
        if (redis.getKeyPrefix() != null) {
            configuration = configuration.computePrefixWith(
                    cacheName -> redis.getKeyPrefix() + cacheName + "::");
        }
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configuration)
                .transactionAware()
                .build();
    }

    @Bean(name = "postgresqlCacheDataSource", destroyMethod = "close", defaultCandidate = false)
    @ConditionalOnExpression(POSTGRESQL_SELECTED)
    @ConditionalOnProperty(prefix = "scaffold.cache.postgresql.datasource", name = "url")
    @ConditionalOnMissingBean(name = "postgresqlCacheDataSource")
    public DataSource postgresqlCacheDataSource(ScaffoldCacheProperties properties) {
        return properties.postgresql().getDatasource().initializeDataSourceBuilder().build();
    }

    @Bean(name = "postgresqlJdbcTemplate", defaultCandidate = false)
    @ConditionalOnExpression(POSTGRESQL_SELECTED)
    @ConditionalOnBean(name = "postgresqlCacheDataSource")
    @ConditionalOnMissingBean(name = "postgresqlJdbcTemplate")
    public JdbcTemplate postgresqlJdbcTemplate(
            @Qualifier("postgresqlCacheDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @ConditionalOnExpression(POSTGRESQL_SELECTED)
    @ConditionalOnBean(name = "postgresqlJdbcTemplate")
    @ConditionalOnMissingBean(PostgresqlCacheStore.class)
    public PostgresqlCacheStore postgresqlCacheStore(
            @Qualifier("postgresqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new PostgresqlCacheStore(jdbcTemplate);
    }

    @Bean("postgresqlCacheManager")
    @ConditionalOnExpression(POSTGRESQL_SELECTED)
    @ConditionalOnBean(PostgresqlCacheStore.class)
    @ConditionalOnMissingBean(name = "postgresqlCacheManager")
    public PostgresqlCacheManager postgresqlCacheManager(PostgresqlCacheStore cacheStore,
                                                         ScaffoldCacheProperties properties,
                                                         PostgresqlCacheSerializer serializer) {
        return new PostgresqlCacheManager(cacheStore, properties.postgresql(), serializer);
    }

    @Bean
    @ConditionalOnMissingBean
    public PostgresqlCacheSerializer postgresqlCacheSerializer(
            @Qualifier("redisObjectMapper") ObjectProvider<ObjectMapper> redisObjectMapperProvider) {
        ObjectMapper objectMapper = redisObjectMapperProvider.getIfAvailable(
                JsonUtil::createRedisObjectMapper);
        return new PostgresqlCacheSerializer(objectMapper);
    }

    @Bean("cacheManager")
    @Primary
    @ConditionalOnMissingBean(name = "cacheManager")
    public CacheManager cacheManager(
            ScaffoldCacheProperties properties,
            @Qualifier("caffeineCacheManager") CacheManager caffeine,
            @Qualifier("redisCacheManager") ObjectProvider<CacheManager> redis,
            @Qualifier("postgresqlCacheManager") ObjectProvider<CacheManager> postgresql) {
        if (properties.mode() == ScaffoldCacheProperties.Mode.TWO_LEVEL) {
            Assert.state(properties.secondary() != ScaffoldCacheProperties.Provider.CAFFEINE,
                    "scaffold.cache.secondary must be REDIS or POSTGRESQL in two-level mode");
            return new TwoLevelCacheManager(caffeine,
                    provider(properties.secondary(), caffeine, redis, postgresql));
        }
        return provider(properties.type(), caffeine, redis, postgresql);
    }

    @Bean("postgresqlCacheTaskScheduler")
    @ConditionalOnBean(PostgresqlCacheManager.class)
    @ConditionalOnMissingBean(name = "postgresqlCacheTaskScheduler")
    public TaskScheduler postgresqlCacheTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("postgresql-cache-cleaner-");
        scheduler.setDaemon(true);
        return scheduler;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnBean(PostgresqlCacheManager.class)
    @ConditionalOnMissingBean(PostgresqlCacheCleaner.class)
    public PostgresqlCacheCleaner postgresqlCacheCleaner(
            PostgresqlCacheStore store,
            @Qualifier("postgresqlCacheTaskScheduler") TaskScheduler scheduler,
            ScaffoldCacheProperties properties) {
        return new PostgresqlCacheCleaner(store, scheduler, properties.postgresql());
    }

    private CacheManager provider(ScaffoldCacheProperties.Provider provider,
                                  CacheManager caffeine,
                                  ObjectProvider<CacheManager> redis,
                                  ObjectProvider<CacheManager> postgresql) {
        return switch (provider) {
            case CAFFEINE -> caffeine;
            case REDIS -> required(redis, "RedisConnectionFactory", provider);
            case POSTGRESQL -> required(postgresql,
                    "scaffold.cache.postgresql.datasource, 'postgresqlCacheDataSource', "
                            + "or PostgresqlCacheStore", provider);
        };
    }

    private CacheManager required(ObjectProvider<CacheManager> manager,
                                  String requirement,
                                  ScaffoldCacheProperties.Provider provider) {
        CacheManager value = manager.getIfAvailable();
        Assert.state(value != null, provider + " cache requires " + requirement);
        return value;
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
