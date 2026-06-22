package com.scaffold.postgresql.cache.starter;

import com.scaffold.postgresql.cache.PostgresqlCacheCleaner;
import com.scaffold.postgresql.cache.PostgresqlCacheInvalidationService;
import com.scaffold.postgresql.cache.PostgresqlCacheManager;
import com.scaffold.postgresql.cache.PostgresqlCacheStore;
import com.scaffold.postgresql.cache.ScaffoldCacheProperties;
import com.scaffold.postgresql.cache.ScaffoldCaffeineCacheManager;
import com.scaffold.postgresql.cache.TwoLevelCacheManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.sql.DataSource;

@Slf4j
@EnableCaching
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(ScaffoldCacheProperties.class)
public class PostgresqlCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager(ObjectProvider<PostgresqlCacheStore> cacheStoreProvider,
                                     PostgresqlCacheInvalidationService invalidationService,
                                     ScaffoldCacheProperties properties) {
        return switch (properties.getMode()) {
            case CAFFEINE -> new ScaffoldCaffeineCacheManager(properties.getCaffeine());
            case POSTGRESQL -> new PostgresqlCacheManager(requiredCacheStore(cacheStoreProvider), properties.getPostgresql());
            case TWO_LEVEL -> new TwoLevelCacheManager(requiredCacheStore(cacheStoreProvider), properties, invalidationService);
        };
    }

    private PostgresqlCacheStore requiredCacheStore(ObjectProvider<PostgresqlCacheStore> cacheStoreProvider) {
        PostgresqlCacheStore cacheStore = cacheStoreProvider.getIfAvailable();
        if (cacheStore == null) {
            throw new IllegalStateException("PostgreSQL cache mode requires a JdbcTemplate bean");
        }
        return cacheStore;
    }

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnMissingBean(PostgresqlCacheStore.class)
    public PostgresqlCacheStore postgresqlCacheStore(JdbcTemplate jdbcTemplate) {
        return new PostgresqlCacheStore(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "postgresqlCacheTaskScheduler")
    public TaskScheduler postgresqlCacheTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("postgresql-cache-cleaner-");
        taskScheduler.setDaemon(true);
        return taskScheduler;
    }

    @Bean
    public PostgresqlCacheInvalidationService postgresqlCacheInvalidationService(ObjectProvider<DataSource> dataSourceProvider,
                                                                                ObjectProvider<PostgresqlCacheStore> cacheStoreProvider,
                                                                                @Qualifier("postgresqlCacheTaskScheduler")
                                                                                TaskScheduler postgresqlCacheTaskScheduler,
                                                                                ScaffoldCacheProperties properties) {
        return new PostgresqlCacheInvalidationService(
                dataSourceProvider,
                cacheStoreProvider,
                postgresqlCacheTaskScheduler,
                properties
        );
    }

    @Bean
    public SmartLifecycle postgresqlCacheInvalidationLifecycle(PostgresqlCacheInvalidationService invalidationService,
                                                              ScaffoldCacheProperties properties) {
        return new SmartLifecycle() {
            private boolean running;

            @Override
            public void start() {
                if (properties.getMode() == ScaffoldCacheProperties.CacheMode.TWO_LEVEL && properties.getClusterInvalidation().isEnabled()) {
                    invalidationService.start();
                }
                running = true;
            }

            @Override
            public void stop() {
                invalidationService.stop();
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }
        };
    }

    @Bean
    public SmartLifecycle postgresqlCacheCleanerLifecycle(ObjectProvider<PostgresqlCacheStore> cacheStoreProvider,
                                                         @Qualifier("postgresqlCacheTaskScheduler")
                                                         TaskScheduler postgresqlCacheTaskScheduler,
                                                         ScaffoldCacheProperties properties) {
        return new SmartLifecycle() {
            private PostgresqlCacheCleaner cleaner;
            private boolean running;

            @Override
            public void start() {
                if (properties.getMode() == ScaffoldCacheProperties.CacheMode.CAFFEINE) {
                    running = true;
                    return;
                }
                cleaner = new PostgresqlCacheCleaner(
                        requiredCacheStore(cacheStoreProvider),
                        postgresqlCacheTaskScheduler,
                        properties
                );
                cleaner.start();
                running = true;
            }

            @Override
            public void stop() {
                if (cleaner != null) {
                    cleaner.stop();
                }
                running = false;
            }

            @Override
            public boolean isRunning() {
                return running;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(KeyGenerator.class)
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(CacheErrorHandler.class)
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("【{}】postgresql cache get error:{}", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("【{}】postgresql cache put error:{}", key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("【{}】postgresql cache evict error:{}", key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("postgresql cache clear error:{}", exception.getMessage());
            }
        };
    }
}
