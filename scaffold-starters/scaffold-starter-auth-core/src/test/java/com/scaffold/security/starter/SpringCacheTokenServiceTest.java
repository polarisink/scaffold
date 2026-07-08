package com.scaffold.security.starter;

import com.scaffold.security.config.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SpringCacheTokenServiceTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class, AuthCoreAutoConfiguration.class))
            .withPropertyValues("security.token.jwt-secret=0123456789abcdef0123456789abcdef");

    @Test
    void shouldStoreReadAndEvictTokenThroughSpringCache() {
        contextRunner.run(context -> {
            TokenService tokenService = context.getBean(TokenService.class);

            tokenService.set("1", "token-value");

            assertThat(tokenService.get("1")).isEqualTo("token-value");
            assertThat(tokenService.has("1")).isTrue();

            tokenService.del("1");

            assertThat(tokenService.get("1")).isNull();
            assertThat(tokenService.has("1")).isFalse();
        });
    }
}
