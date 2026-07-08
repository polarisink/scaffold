package com.scaffold.security.starter;

import com.scaffold.security.config.TokenService;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableCaching
@AutoConfiguration(before = CacheAutoConfiguration.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class AuthCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService(CacheManager cacheManager) {
        return new SpringCacheTokenService(cacheManager);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
