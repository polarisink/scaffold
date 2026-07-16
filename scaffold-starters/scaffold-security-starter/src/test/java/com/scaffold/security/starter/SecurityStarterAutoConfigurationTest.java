package com.scaffold.security.starter;

import com.scaffold.security.config.TokenAuthenticationFilter;
import com.scaffold.security.config.TokenStore;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityStarterAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withPropertyValues("scaffold.security.token.jwt-secret=0123456789abcdef0123456789abcdef")
            .withUserConfiguration(TestSecuritySupport.class)
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class,
                    WebMvcAutoConfiguration.class,
                    UserDetailsServiceAutoConfiguration.class,
                    CacheAutoConfiguration.class,
                    SecurityStarterAutoConfiguration.class
            ));

    @Test
    void shouldUseSpringCacheTokenStoreByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(TokenStore.class);
            assertThat(AopUtils.getTargetClass(context.getBean(TokenStore.class)))
                    .isEqualTo(SpringCacheTokenStore.class);
            assertThat(context).hasSingleBean(TokenAuthenticationFilter.class);
        });
    }

    @Test
    void shouldUseSpringCacheTokenStoreWhenLegacyRedisStoreRequested() {
        contextRunner
                .withClassLoader(new FilteredClassLoader("org.redisson"))
                .withPropertyValues("scaffold.security.token.store-type=redis")
                .run(context -> {
                    assertThat(context).hasSingleBean(TokenStore.class);
                    assertThat(AopUtils.getTargetClass(context.getBean(TokenStore.class)))
                            .isEqualTo(SpringCacheTokenStore.class);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class TestSecuritySupport {

        @Bean
        UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(User.withUsername("tester").password("{noop}secret").authorities("demo").build());
        }
    }
}
