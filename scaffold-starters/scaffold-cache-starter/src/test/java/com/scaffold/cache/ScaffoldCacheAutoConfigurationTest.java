package com.scaffold.cache;

import com.scaffold.postgresql.PostgresqlCacheManager;
import com.scaffold.postgresql.PostgresqlCacheStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScaffoldCacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ScaffoldCacheAutoConfiguration.class));

    @Test
    void defaultsToSingleCaffeineCache() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("cacheManager");
            assertThat(context.getBean("cacheManager", CacheManager.class))
                    .isInstanceOf(CaffeineCacheManager.class);
        });
    }

    @Test
    void defaultsToCaffeineWhenRedisIsNotOnTheClasspath() {
        contextRunner
                .withClassLoader(new FilteredClassLoader("org.springframework.data.redis"))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean("redisObjectMapper");
                    assertThat(context).doesNotHaveBean("redisCacheManager");
                    assertThat(context.getBean("cacheManager", CacheManager.class))
                            .isInstanceOf(CaffeineCacheManager.class);
                });
    }

    @Test
    void failsFastWhenRedisIsSelectedWithoutConnectionFactory() {
        contextRunner
                .withPropertyValues("scaffold.cache.type=redis")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("RedisConnectionFactory");
                });
    }

    @Test
    void failsFastWhenPostgresqlIsSelectedWithoutDedicatedDatasource() {
        contextRunner
                .withPropertyValues("scaffold.cache.type=postgresql")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("scaffold.cache.postgresql.datasource");
                });
    }

    @Test
    void createsTwoLevelCacheWithPostgresqlSecondLevel() {
        contextRunner
                .withBean(PostgresqlCacheStore.class, () -> mock(PostgresqlCacheStore.class))
                .withPropertyValues(
                        "scaffold.cache.mode=two-level",
                        "scaffold.cache.secondary=postgresql",
                        "scaffold.cache.postgresql.initialize-schema=false",
                        "scaffold.cache.postgresql.cleanup-on-startup=false",
                        "scaffold.cache.postgresql.scheduled-cleanup=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(PostgresqlCacheManager.class);
                    assertThat(context.getBean("cacheManager", CacheManager.class))
                            .isInstanceOf(TwoLevelCacheManager.class);
                });
    }

    @Test
    void requiresDedicatedDatasourceEvenWhenSystemJdbcTemplateIsPostgresql() {
        contextRunner
                .withBean(JdbcTemplate.class, () -> mock(JdbcTemplate.class))
                .withPropertyValues("scaffold.cache.type=postgresql")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("scaffold.cache.postgresql.datasource");
                });
    }

    @Test
    void ignoresSystemJdbcTemplateWhenPostgresqlIsNotSelected() {
        contextRunner
                .withBean(JdbcTemplate.class, () -> mock(JdbcTemplate.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(PostgresqlCacheStore.class);
                    assertThat(context.getBean("cacheManager", CacheManager.class))
                            .isInstanceOf(CaffeineCacheManager.class);
                });
    }

    @Test
    void usesApplicationProvidedDedicatedPostgresqlDatasource() {
        DataSource dedicated = dataSource("PostgreSQL");
        contextRunner
                .withBean("postgresqlCacheDataSource", DataSource.class, () -> dedicated)
                .withPropertyValues(
                        "scaffold.cache.type=postgresql",
                        "scaffold.cache.postgresql.initialize-schema=false",
                        "scaffold.cache.postgresql.cleanup-on-startup=false",
                        "scaffold.cache.postgresql.scheduled-cleanup=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean("postgresqlCacheDataSource")).isSameAs(dedicated);
                    PostgresqlCacheStore store = context.getBean(PostgresqlCacheStore.class);
                    JdbcTemplate jdbcTemplate = (JdbcTemplate) ReflectionTestUtils.getField(store, "jdbcTemplate");
                    assertThat(jdbcTemplate.getDataSource()).isSameAs(dedicated);
                });
    }

    @Test
    void createsDedicatedPostgresqlDatasourceFromProperties() {
        contextRunner
                .withBean(PostgresqlCacheStore.class, () -> mock(PostgresqlCacheStore.class))
                .withPropertyValues(
                        "scaffold.cache.type=postgresql",
                        "scaffold.cache.postgresql.datasource.url=jdbc:postgresql://localhost/cache",
                        "scaffold.cache.postgresql.datasource.username=cache_user",
                        "scaffold.cache.postgresql.datasource.password=secret",
                        "scaffold.cache.postgresql.datasource.driver-class-name=org.postgresql.Driver",
                        "scaffold.cache.postgresql.initialize-schema=false",
                        "scaffold.cache.postgresql.cleanup-on-startup=false",
                        "scaffold.cache.postgresql.scheduled-cleanup=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasBean("postgresqlCacheDataSource");
                    assertThat(context.getBean("postgresqlCacheDataSource"))
                            .isInstanceOf(DataSource.class);
                    assertThat(context).hasBean("postgresqlJdbcTemplate");
                    assertThat(context.getBean(ScaffoldCacheProperties.class)
                            .postgresql().getDatasource().getUrl())
                            .isEqualTo("jdbc:postgresql://localhost/cache");
                });
    }

    @Test
    void dedicatedPostgresqlDatasourceDoesNotCompeteWithBusinessDatasource() {
        DataSource businessDataSource = dataSource("MySQL");
        contextRunner
                .withUserConfiguration(BusinessDataSourceConfiguration.class)
                .withBean("businessDataSource", DataSource.class, () -> businessDataSource)
                .withBean(PostgresqlCacheStore.class, () -> mock(PostgresqlCacheStore.class))
                .withPropertyValues(
                        "scaffold.cache.type=postgresql",
                        "scaffold.cache.postgresql.datasource.url=jdbc:postgresql://localhost/cache",
                        "scaffold.cache.postgresql.datasource.driver-class-name=org.postgresql.Driver",
                        "scaffold.cache.postgresql.initialize-schema=false",
                        "scaffold.cache.postgresql.cleanup-on-startup=false",
                        "scaffold.cache.postgresql.scheduled-cleanup=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasBean("postgresqlCacheDataSource");
                    assertThat(context).hasBean("businessDataSourceConsumer");
                    assertThat(context.getBean("businessDataSourceConsumer"))
                            .isSameAs(businessDataSource);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class BusinessDataSourceConfiguration {

        @Bean
        @ConditionalOnSingleCandidate(DataSource.class)
        DataSource businessDataSourceConsumer(DataSource dataSource) {
            return dataSource;
        }
    }

    private static DataSource dataSource(String productName) {
        DataSource dataSource = mock(DataSource.class);
        try {
            Connection connection = mock(Connection.class);
            DatabaseMetaData metadata = mock(DatabaseMetaData.class);
            when(connection.getMetaData()).thenReturn(metadata);
            when(metadata.getDatabaseProductName()).thenReturn(productName);
            when(dataSource.getConnection()).thenReturn(connection);
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
        return dataSource;
    }
}
