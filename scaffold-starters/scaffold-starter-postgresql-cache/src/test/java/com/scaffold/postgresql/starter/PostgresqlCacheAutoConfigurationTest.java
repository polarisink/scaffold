package com.scaffold.postgresql.starter;

import com.scaffold.postgresql.PostgresqlCacheCleaner;
import com.scaffold.postgresql.PostgresqlCacheManager;
import com.scaffold.postgresql.PostgresqlCacheProperties;
import com.scaffold.postgresql.PostgresqlCacheStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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
