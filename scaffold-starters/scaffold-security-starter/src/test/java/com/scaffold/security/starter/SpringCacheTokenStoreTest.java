package com.scaffold.security.starter;

import com.scaffold.security.config.TokenStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.assertj.core.api.Assertions.assertThat;

class SpringCacheTokenStoreTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class,
                    WebMvcAutoConfiguration.class,
                    UserDetailsServiceAutoConfiguration.class,
                    CacheAutoConfiguration.class,
                    SecurityStarterAutoConfiguration.class))
            .withUserConfiguration(TestSecuritySupport.class)
            .withPropertyValues("scaffold.security.token.jwt-secret=0123456789abcdef0123456789abcdef");

    @Test
    void shouldStoreReadAndEvictTokenThroughSpringCache() {
        contextRunner.run(context -> {
            TokenStore tokenStore = context.getBean(TokenStore.class);
            assertThat(tokenStore.set("1", "token-value")).isEqualTo("token-value");
            assertThat(tokenStore.get("1")).isEqualTo("token-value");
            assertThat(context.getBean(CacheManager.class).getCache(TokenStore.TOKEN_CACHE_NAME)).isNotNull();
            tokenStore.del("1");
            assertThat(tokenStore.get("1")).isNull();
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class TestSecuritySupport {

        @Bean
        UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(
                    User.withUsername("tester").password("{noop}secret").authorities("demo").build());
        }
    }
}
