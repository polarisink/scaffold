package com.scaffold.core.base.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author lqsgo
 */
@Configuration
public class RestClientConfig {
    /**
     * 创建代理服务
     *
     * @param builder builder
     * @param baseUrl 基础路径
     * @param tClass  代理类
     * @param <T>     泛型
     * @return 代理service
     */
    public static <T> T createService(RestClient.Builder builder, String baseUrl, Class<T> tClass) {
        RestClient restClient = builder.baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(tClass);
    }

    @Bean
    public RestClient.Builder builder(ObjectMapper objectMapper, ClientHttpRequestInterceptor interceptor) {
        return RestClient.builder()
                //消息转换器
                .messageConverters(converters -> {
                    converters.removeIf(c -> c.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
                    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                    converter.setObjectMapper(objectMapper);
                    converters.addFirst(converter);
                })
                //拦截器
                .requestInterceptors(interceptors -> interceptors.add(interceptor));
    }
}
