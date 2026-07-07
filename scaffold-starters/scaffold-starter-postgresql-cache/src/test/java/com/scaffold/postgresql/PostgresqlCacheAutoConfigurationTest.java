package com.scaffold.postgresql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PostgresqlCacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PostgresqlCacheAutoConfiguration.class))
            .withBean(JdbcTemplate.class, () -> mock(JdbcTemplate.class));

    @Test
    void providesPostgresqlStoreWithoutTakingOwnershipOfCacheManager() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PostgresqlCacheStore.class);
            assertThat(context).doesNotHaveBean(CacheManager.class);
            assertThat(context).doesNotHaveBean(PostgresqlCacheCleaner.class);
        });
    }

    @Test
    void backsOffWhenPostgresqlDriverIsMissing() {
        contextRunner
                .withClassLoader(new FilteredClassLoader("org.postgresql"))
                .run(context -> assertThat(context).doesNotHaveBean(PostgresqlCacheStore.class));
    }

    @Test
    void prefersNamedPostgresqlJdbcTemplateWhenAvailable() {
        contextRunner
                .withBean("postgresqlJdbcTemplate", JdbcTemplate.class, () -> mock(JdbcTemplate.class))
                .run(context -> {
                    PostgresqlCacheStore cacheStore = context.getBean(PostgresqlCacheStore.class);
                    assertThat(ReflectionTestUtils.getField(cacheStore, "jdbcTemplate"))
                            .isSameAs(context.getBean("postgresqlJdbcTemplate", JdbcTemplate.class));
                });
    }

    @Test
    void failsFastWhenMultipleJdbcTemplatesExistWithoutNamedPostgresqlJdbcTemplate() {
        contextRunner
                .withBean("mysqlJdbcTemplate", JdbcTemplate.class, () -> mock(JdbcTemplate.class))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("Define a bean named 'postgresqlJdbcTemplate'");
                });
    }

    @Test
    void usesPostgresqlCacheOnlyWhenApplicationProvidesItsCacheManager() {
        contextRunner
                .withPropertyValues("scaffold.cache.postgresql.default-ttl=1h")
                .withUserConfiguration(PostgresqlCacheConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(CacheManager.class);
                    assertThat(context.getBean(CacheManager.class)).isInstanceOf(PostgresqlCacheManager.class);
                    assertThat(context).hasSingleBean(PostgresqlCacheCleaner.class);
                    assertThat(context.getBean(PostgresqlCacheProperties.class).getDefaultTtl())
                            .isEqualTo(Duration.ofHours(1));
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class PostgresqlCacheConfiguration {

        @Bean
        PostgresqlCacheManager cacheManager(PostgresqlCacheStore cacheStore,
                                            PostgresqlCacheProperties properties) {
            return new PostgresqlCacheManager(cacheStore, properties);
        }
    }
}
