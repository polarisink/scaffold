package com.scaffold.security.starter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.scaffold.security.config.SecurityConfig;
import com.scaffold.security.config.TokenAuthenticationFilter;
import com.scaffold.security.config.TokenStore;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@EnableCaching
@AutoConfiguration(before = CacheAutoConfiguration.class)
@Import(SecurityConfig.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityStarterAutoConfiguration {

    @Bean
    @ConditionalOnClass(CaffeineCacheManager.class)
    public CacheManagerCustomizer<CaffeineCacheManager> securityTokenCaffeineCacheCustomizer(
            SecurityProperties securityProperties) {
        return cacheManager -> cacheManager.registerCustomCache(
                TokenStore.TOKEN_CACHE_NAME,
                Caffeine.newBuilder()
                        .expireAfterWrite(securityProperties.getToken().getCacheTtl())
                        .maximumSize(securityProperties.getToken().getCacheMaximumSize())
                        .build());
    }

    @Bean
    @ConditionalOnMissingBean(TokenStore.class)
    public TokenStore tokenStore(CacheManager cacheManager) {
        return new SpringCacheTokenStore(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

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
