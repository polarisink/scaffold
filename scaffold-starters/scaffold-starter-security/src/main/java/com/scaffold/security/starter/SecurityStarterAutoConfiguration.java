package com.scaffold.security.starter;

import com.scaffold.security.config.SecurityConfig;
import com.scaffold.security.config.TokenAndLogFilter;
import com.scaffold.security.config.TokenService;
import com.scaffold.security.config.TokenServiceImpl;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@AutoConfiguration
@Import(SecurityConfig.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }

    @Bean("tokenAndLogFilter")
    @ConditionalOnMissingBean
    public TokenAndLogFilter tokenAndLogFilter(PathMatcher pathMatcher, TokenService tokenService) {
        return new TokenAndLogFilter(pathMatcher, tokenService);
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    @ConditionalOnProperty(prefix = "security.token", name = "store-type", havingValue = "memory", matchIfMissing = true)
    public TokenService inMemoryTokenService(SecurityProperties securityProperties) {
        return new InMemoryTokenService(securityProperties.getToken().getTtlMinutes());
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnProperty(prefix = "security.token", name = "store-type", havingValue = "redis")
    public TokenService redisTokenService(SecurityProperties securityProperties) {
        return new RedisTokenService(securityProperties.getToken().getTtlMinutes());
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService() {
        return new TokenServiceImpl();
    }
}
