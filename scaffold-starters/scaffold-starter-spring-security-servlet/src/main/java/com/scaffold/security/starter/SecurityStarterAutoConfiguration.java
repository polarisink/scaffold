package com.scaffold.security.starter;

import com.scaffold.security.config.SecurityConfig;
import com.scaffold.security.config.TokenAndLogFilter;
import com.scaffold.security.config.TokenService;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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
}
