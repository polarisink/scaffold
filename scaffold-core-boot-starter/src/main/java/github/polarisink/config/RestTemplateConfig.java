package github.polarisink.config;

import github.polarisink.log.LogClientHttpRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


/**
 * http 请求工具注入
 *
 * @author lqs
 */
@Configuration
@ConditionalOnMissingBean(RestTemplateAutoConfiguration.class)
public class RestTemplateConfig {

	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(10000);
		factory.setReadTimeout(10000);
		return factory;
	}


	@Bean
	public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {

		RestTemplate restTemplate = new RestTemplate();
		/**
		 * StringHttpMessageConverter 默认使用ISO-8859-1编码，此处修改为UTF-8
		 */
		List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
		messageConverters.stream()
			.filter(StringHttpMessageConverter.class::isInstance)
			.forEachOrdered(converter -> ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8));

		//Interceptors 添加写的 Interceptors
		restTemplate.setInterceptors(Collections.singletonList(new LogClientHttpRequestInterceptor()));
		//BufferingClientHttpRequestFactory  此处替换为BufferingClientHttpRequestFactory
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));
		return restTemplate;
	}
}
