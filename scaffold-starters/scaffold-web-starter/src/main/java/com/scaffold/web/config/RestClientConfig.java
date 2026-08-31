package com.scaffold.web.config;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
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

    public RestClient.Builder builder(JsonMapper jsonMapper, ClientHttpRequestInterceptor interceptor) {
        return RestClient.builder()
                // 消息转换器
                .configureMessageConverters(converters -> converters
                        .registerDefaults()
                        .withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper)))
                // 拦截器
                .requestInterceptors(interceptors -> interceptors.add(interceptor));
    }
}
