package com.lqs.scaffold.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author lqs
 * @describe
 * @date 2021/11/16
 */
@Configuration
public class CacheConfig {
	@Bean("myKeyGenerator")
	public KeyGenerator keyGenerator() {
		return (target, method, params) -> method.getName() + "[" + Arrays.asList(params) + "]";
	}
}
