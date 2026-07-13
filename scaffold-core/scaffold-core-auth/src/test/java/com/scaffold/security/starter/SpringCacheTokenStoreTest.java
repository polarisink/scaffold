package com.scaffold.security.starter;

import com.scaffold.security.config.TokenStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

class SpringCacheTokenStoreTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class, AuthCoreAutoConfiguration.class))
            .withPropertyValues("scaffold.security.token.jwt-secret=0123456789abcdef0123456789abcdef");

    @Test
    void shouldStoreReadAndEvictTokenThroughSpringCache() {
        contextRunner.run(context -> {
            TokenStore tokenStore = context.getBean(TokenStore.class);

            assertThat(tokenStore.set("1", "token-value")).isEqualTo("token-value");

            assertThat(tokenStore.get("1")).isEqualTo("token-value");
            assertThat(tokenStore.has("1")).isTrue();
            assertThat(context.getBean(CacheManager.class)
                    .getCache(TokenStore.TOKEN_CACHE_NAME)
                    .get("1", String.class))
                    .isEqualTo("token-value");

            tokenStore.del("1");

            assertThat(tokenStore.get("1")).isNull();
            assertThat(tokenStore.has("1")).isFalse();
        });
    }

    @Test
    void shouldUseIndependentTtlForSecurityTokenCache() {
        contextRunner
                .withPropertyValues(
                        "spring.cache.type=caffeine",
                        "spring.cache.caffeine.spec=maximumSize=10000,expireAfterWrite=1h",
                        "scaffold.security.token.cache-ttl=20ms")
                .run(context -> {
                    TokenStore tokenStore = context.getBean(TokenStore.class);

                    tokenStore.set("1", "token-value");
                    assertThat(tokenStore.get("1")).isEqualTo("token-value");

                    Thread.sleep(80);
                    cleanUpTokenCache(context.getBean(CacheManager.class));

                    assertThat(tokenStore.get("1")).isNull();
                });
    }

    @SuppressWarnings("unchecked")
    private void cleanUpTokenCache(CacheManager cacheManager) {
        Cache cache = cacheManager.getCache(TokenStore.TOKEN_CACHE_NAME);
        assertThat(cache).isInstanceOf(org.springframework.cache.caffeine.CaffeineCache.class);
        ((org.springframework.cache.caffeine.CaffeineCache) cache)
                .getNativeCache()
                .cleanUp();
    }
}
