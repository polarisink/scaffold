package com.scaffold.postgresql;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;

@AutoConfiguration
@ConditionalOnClass(name = "org.postgresql.Driver")
@EnableConfigurationProperties(PostgresqlCacheProperties.class)
public class PostgresqlCacheAutoConfiguration {

    @Bean
    @ConditionalOnBean(name = "postgresqlJdbcTemplate")
    @ConditionalOnMissingBean(PostgresqlCacheStore.class)
    public PostgresqlCacheStore postgresqlCacheStore(
            @Qualifier("postgresqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
        return new PostgresqlCacheStore(jdbcTemplate);
    }

    @Bean
    @ConditionalOnBean(JdbcTemplate.class)
    @ConditionalOnMissingBean(value = PostgresqlCacheStore.class, name = "postgresqlJdbcTemplate")
    public PostgresqlCacheStore defaultPostgresqlCacheStore(ListableBeanFactory beanFactory) {
        String[] jdbcTemplateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                beanFactory, JdbcTemplate.class, false, false
        );
        Assert.state(jdbcTemplateNames.length == 1,
                "Multiple JdbcTemplate beans found. Define a bean named 'postgresqlJdbcTemplate' "
                        + "or provide a PostgresqlCacheStore bean for PostgreSQL cache.");
        return new PostgresqlCacheStore(beanFactory.getBean(jdbcTemplateNames[0], JdbcTemplate.class));
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
