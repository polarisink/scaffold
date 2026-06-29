package com.scaffold.security.starter;

import com.scaffold.security.config.TokenService;
import com.scaffold.security.config.TokenServiceImpl;
import com.scaffold.security.vo.SecurityProperties;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class AuthCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    @ConditionalOnProperty(prefix = "security.token", name = "store-type", havingValue = "memory", matchIfMissing = true)
    public TokenService inMemoryTokenService(SecurityProperties securityProperties) {
        return new InMemoryTokenService(securityProperties.getToken().getTtlMinutes());
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService() {
        return new TokenServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RedissonClient.class)
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = "security.token", name = "store-type", havingValue = "redis")
    static class RedisTokenConfiguration {

        @Bean
        @ConditionalOnMissingBean(TokenService.class)
        TokenService redisTokenService(SecurityProperties securityProperties) {
            return new RedisTokenService(securityProperties.getToken().getTtlMinutes());
        }
    }
}
