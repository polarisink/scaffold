package com.scaffold.security.starter;

import com.scaffold.security.config.TokenService;
import com.scaffold.security.config.TokenAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityStarterAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withPropertyValues("security.token.jwt-secret=0123456789abcdef0123456789abcdef")
            .withUserConfiguration(TestSecuritySupport.class)
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class,
                    WebMvcAutoConfiguration.class,
                    UserDetailsServiceAutoConfiguration.class,
                    AuthCoreAutoConfiguration.class,
                    SecurityStarterAutoConfiguration.class
            ));

    @Test
    void shouldUseInMemoryTokenStoreByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(TokenService.class);
            assertThat(context.getBean(TokenService.class)).isInstanceOf(InMemoryTokenService.class);
            assertThat(context).hasSingleBean(TokenAuthenticationFilter.class);
        });
    }

    @Test
    void shouldFailWhenRedisStoreRequestedWithoutRedisson() {
        contextRunner
                .withClassLoader(new FilteredClassLoader("org.redisson"))
                .withPropertyValues("security.token.store-type=redis")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure())
                            .hasRootCauseInstanceOf(NoSuchBeanDefinitionException.class);
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
