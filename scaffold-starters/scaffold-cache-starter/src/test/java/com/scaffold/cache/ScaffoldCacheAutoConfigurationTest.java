package com.scaffold.cache;

import com.scaffold.postgresql.PostgresqlCacheManager;
import com.scaffold.postgresql.PostgresqlCacheStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
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
    void failsFastWhenRedisIsSelectedWithoutConnectionFactory() {
        contextRunner
                .withPropertyValues("scaffold.cache.provider=redis")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("RedisConnectionFactory");
                });
    }

    @Test
    void failsFastWhenPostgresqlIsSelectedWithoutExplicitStore() {
        contextRunner
                .withPropertyValues("scaffold.cache.provider=postgresql")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("postgresqlJdbcTemplate");
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
    void usesSystemJdbcTemplateWhenItIsPostgresql() {
        contextRunner
                .withBean(JdbcTemplate.class, () -> jdbcTemplate("PostgreSQL"))
                .withPropertyValues(
                        "scaffold.cache.provider=postgresql",
                        "scaffold.cache.postgresql.initialize-schema=false",
                        "scaffold.cache.postgresql.cleanup-on-startup=false",
                        "scaffold.cache.postgresql.scheduled-cleanup=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(PostgresqlCacheStore.class);
                    assertThat(context.getBean("cacheManager", CacheManager.class))
                            .isInstanceOf(PostgresqlCacheManager.class);
                });
    }

    @Test
    void usesJdbcTemplateCreatedBySpringBootForSystemDatasource() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        ScaffoldCacheAutoConfiguration.class,
                        DataSourceAutoConfiguration.class,
                        JdbcTemplateAutoConfiguration.class))
                .withBean(DataSource.class, () -> dataSource("PostgreSQL"))
                .withPropertyValues(
                        "scaffold.cache.provider=postgresql",
                        "scaffold.cache.postgresql.initialize-schema=false",
                        "scaffold.cache.postgresql.cleanup-on-startup=false",
                        "scaffold.cache.postgresql.scheduled-cleanup=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(JdbcTemplate.class);
                    assertThat(context).hasSingleBean(PostgresqlCacheStore.class);
                    assertThat(context.getBean("cacheManager", CacheManager.class))
                            .isInstanceOf(PostgresqlCacheManager.class);
                });
    }

    @Test
    void rejectsSystemJdbcTemplateForAnotherDatabase() {
        contextRunner
                .withBean(JdbcTemplate.class, () -> jdbcTemplate("MySQL"))
                .withPropertyValues("scaffold.cache.provider=postgresql")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasMessageContaining("requires a PostgreSQL JdbcTemplate")
                            .hasMessageContaining("MySQL");
                });
    }

    @Test
    void doesNotValidateSystemDatabaseWhenPostgresqlIsNotSelected() {
        contextRunner
                .withBean(JdbcTemplate.class, () -> jdbcTemplate("MySQL"))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(PostgresqlCacheStore.class);
                    assertThat(context.getBean("cacheManager", CacheManager.class))
                            .isInstanceOf(CaffeineCacheManager.class);
                });
    }

    @Test
    void namedPostgresqlJdbcTemplateTakesPriorityOverSystemTemplate() {
        JdbcTemplate system = jdbcTemplate("MySQL");
        JdbcTemplate external = jdbcTemplate("PostgreSQL");
        contextRunner
                .withBean("systemJdbcTemplate", JdbcTemplate.class, () -> system)
                .withBean("postgresqlJdbcTemplate", JdbcTemplate.class, () -> external)
                .withPropertyValues(
                        "scaffold.cache.provider=postgresql",
                        "scaffold.cache.postgresql.initialize-schema=false",
                        "scaffold.cache.postgresql.cleanup-on-startup=false",
                        "scaffold.cache.postgresql.scheduled-cleanup=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    PostgresqlCacheStore store = context.getBean(PostgresqlCacheStore.class);
                    assertThat(ReflectionTestUtils.getField(store, "jdbcTemplate")).isSameAs(external);
                });
    }

    @Test
    void createsDedicatedPostgresqlDatasourceFromProperties() {
        contextRunner
                .withBean(PostgresqlCacheStore.class, () -> mock(PostgresqlCacheStore.class))
                .withPropertyValues(
                        "scaffold.cache.provider=postgresql",
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static JdbcTemplate jdbcTemplate(String productName) {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        try {
            Connection connection = mock(Connection.class);
            DatabaseMetaData metadata = mock(DatabaseMetaData.class);
            when(connection.getMetaData()).thenReturn(metadata);
            when(metadata.getDatabaseProductName()).thenReturn(productName);
            when(jdbcTemplate.execute(any(ConnectionCallback.class))).thenAnswer(invocation ->
                    ((ConnectionCallback) invocation.getArgument(0)).doInConnection(connection));
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
        return jdbcTemplate;
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
