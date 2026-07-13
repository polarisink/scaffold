package com.scaffold.security.starter;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.scaffold.security.config.TokenStore;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableCaching
@AutoConfiguration(before = CacheAutoConfiguration.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class AuthCoreAutoConfiguration {

    @Bean
    @ConditionalOnClass(CaffeineCacheManager.class)
    public CacheManagerCustomizer<CaffeineCacheManager> securityTokenCaffeineCacheCustomizer(
            SecurityProperties securityProperties) {
        return cacheManager -> cacheManager.registerCustomCache(
                TokenStore.TOKEN_CACHE_NAME,
                Caffeine.newBuilder()
                        .expireAfterWrite(securityProperties.getToken().getCacheTtl())
                        .maximumSize(securityProperties.getToken().getCacheMaximumSize())
                        .build()
        );
    }

    @Bean
    @ConditionalOnMissingBean(TokenStore.class)
    public TokenStore tokenService(CacheManager cacheManager) {
        return new SpringCacheTokenStore(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
