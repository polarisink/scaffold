package com.scaffold.security.starter;

import com.scaffold.security.config.SecurityConfig;
import com.scaffold.security.config.TokenAuthenticationFilter;
import com.scaffold.security.config.TokenStore;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@AutoConfiguration(after = AuthCoreAutoConfiguration.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PathMatcher pathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil(SecurityProperties securityProperties) {
        return new JwtUtil(securityProperties.getToken().getJwtSecret());
    }

    @Bean("tokenAuthenticationFilter")
    @ConditionalOnMissingBean
    public TokenAuthenticationFilter tokenAuthenticationFilter(
            PathMatcher pathMatcher,
            TokenStore tokenStore,
            SecurityProperties securityProperties,
            JwtUtil jwtUtil) {
        return new TokenAuthenticationFilter(pathMatcher, tokenStore, securityProperties, jwtUtil);
    }
}
