package com.lqs.scaffold.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lqs.scaffold.core.AuthenticationAttributeResolver;
import com.lqs.scaffold.property.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * MVC Configuration
 *
 * @author Bill
 * @version 1.0
 * @since 2019-10-24
 */
@Slf4j
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

	private final CorsProperties corsProperties;
	private final TestInterceptor testInterceptor;

	public MvcConfiguration(CorsProperties corsProperties, TestInterceptor testInterceptor) {
		this.corsProperties = corsProperties;
		this.testInterceptor = testInterceptor;
	}

	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
		return new StringHttpMessageConverter(Charset.forName(StandardCharsets.UTF_8.name()));
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public MappingJackson2HttpMessageConverter messageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(getObjectMapper());
		return converter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(responseBodyConverter());
		converters.add(messageConverter());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new AuthenticationAttributeResolver());
	}

	/**
	 * 如果实现Filter跨域拦截，此处会无效
	 *
	 * @param registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		List<String> allowedOrigins = corsProperties.getAllowedOrigins();
		String[] origins = new String[0];
		origins = allowedOrigins.toArray(origins);
		registry.addMapping("/**")
			.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
			.allowedOrigins(origins).allowCredentials(true);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		log.info("TokenConfig addInterceptors tokenInterceptor");
		registry.addInterceptor(testInterceptor)
			//指定该类拦截的url
			.addPathPatterns("/**")
			//过滤静态资源
			.excludePathPatterns("/static/**");
	}

}
