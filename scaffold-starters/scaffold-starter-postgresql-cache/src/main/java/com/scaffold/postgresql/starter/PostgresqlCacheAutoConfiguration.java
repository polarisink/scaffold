package com.scaffold.postgresql.starter;

import com.scaffold.postgresql.PostgresqlCacheCleaner;
import com.scaffold.postgresql.PostgresqlCacheManager;
import com.scaffold.postgresql.PostgresqlCacheProperties;
import com.scaffold.postgresql.PostgresqlCacheStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@AutoConfiguration
@EnableConfigurationProperties(PostgresqlCacheProperties.class)
public class PostgresqlCacheAutoConfiguration {

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnMissingBean(PostgresqlCacheStore.class)
    public PostgresqlCacheStore postgresqlCacheStore(JdbcTemplate jdbcTemplate) {
        return new PostgresqlCacheStore(jdbcTemplate);
    }

    @Bean
    @ConditionalOnBean(PostgresqlCacheManager.class)
    @ConditionalOnMissingBean(name = "postgresqlCacheTaskScheduler")
    public TaskScheduler postgresqlCacheTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("postgresql-cache-cleaner-");
        taskScheduler.setDaemon(true);
        return taskScheduler;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnBean(PostgresqlCacheManager.class)
    @ConditionalOnMissingBean(PostgresqlCacheCleaner.class)
    public PostgresqlCacheCleaner postgresqlCacheCleaner(
            PostgresqlCacheStore cacheStore,
            @Qualifier("postgresqlCacheTaskScheduler") TaskScheduler taskScheduler,
            PostgresqlCacheProperties properties) {
        return new PostgresqlCacheCleaner(cacheStore, taskScheduler, properties);
    }
}
