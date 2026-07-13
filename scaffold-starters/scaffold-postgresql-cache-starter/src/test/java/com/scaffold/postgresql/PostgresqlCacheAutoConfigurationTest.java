package com.scaffold.postgresql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostgresqlCacheAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PostgresqlCacheAutoConfiguration.class))
            .withBean(JdbcTemplate.class, () -> jdbcTemplate("PostgreSQL"));

    @Test
    void providesPostgresqlStoreAndCacheManager() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PostgresqlCacheStore.class);
            assertThat(context).hasSingleBean(CacheManager.class);
            assertThat(context.getBean(CacheManager.class)).isInstanceOf(PostgresqlCacheManager.class);
            assertThat(context).hasSingleBean(PostgresqlCacheCleaner.class);
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
                .withBean("postgresqlJdbcTemplate", JdbcTemplate.class, () -> jdbcTemplate("PostgreSQL"))
                .run(context -> {
                    PostgresqlCacheStore cacheStore = context.getBean(PostgresqlCacheStore.class);
                    assertThat(ReflectionTestUtils.getField(cacheStore, "jdbcTemplate"))
                            .isSameAs(context.getBean("postgresqlJdbcTemplate", JdbcTemplate.class));
                });
    }

    @Test
    void failsFastWhenMultipleJdbcTemplatesExistWithoutNamedPostgresqlJdbcTemplate() {
        contextRunner
                .withBean("mysqlJdbcTemplate", JdbcTemplate.class, () -> jdbcTemplate("MySQL"))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("Define a bean named 'postgresqlJdbcTemplate'");
                });
    }

    @Test
    void failsFastWhenSingleJdbcTemplateIsNotPostgresql() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(PostgresqlCacheAutoConfiguration.class))
                .withBean(JdbcTemplate.class, () -> jdbcTemplate("MySQL"))
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("PostgreSQL cache requires a PostgreSQL JdbcTemplate");
                });
    }

    @Test
    void backsOffWhenApplicationProvidesCacheManager() {
        CacheManager cacheManager = mock(CacheManager.class);
        contextRunner
                .withBean(CacheManager.class, () -> cacheManager)
                .run(context -> {
                    assertThat(context).hasSingleBean(PostgresqlCacheStore.class);
                    assertThat(context).hasSingleBean(CacheManager.class);
                    assertThat(context.getBean(CacheManager.class)).isSameAs(cacheManager);
                    assertThat(context).doesNotHaveBean(PostgresqlCacheManager.class);
                    assertThat(context).doesNotHaveBean(PostgresqlCacheCleaner.class);
                });
    }

    @Test
    void usesPostgresqlPropertiesForAutoCacheManager() {
        contextRunner
                .withPropertyValues("scaffold.cache.postgresql.default-ttl=1h")
                .run(context -> {
                    assertThat(context).hasSingleBean(CacheManager.class);
                    assertThat(context.getBean(CacheManager.class)).isInstanceOf(PostgresqlCacheManager.class);
                    assertThat(context).hasSingleBean(PostgresqlCacheCleaner.class);
                    assertThat(context.getBean(PostgresqlCacheProperties.class).getDefaultTtl())
                            .isEqualTo(Duration.ofHours(1));
                });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static JdbcTemplate jdbcTemplate(String databaseProductName) {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        try {
            Connection connection = mock(Connection.class);
            DatabaseMetaData metaData = mock(DatabaseMetaData.class);
            when(connection.getMetaData()).thenReturn(metaData);
            when(metaData.getDatabaseProductName()).thenReturn(databaseProductName);
            when(jdbcTemplate.execute(any(ConnectionCallback.class))).thenAnswer(invocation ->
                    ((ConnectionCallback) invocation.getArgument(0)).doInConnection(connection)
            );
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
        return jdbcTemplate;
    }
}
