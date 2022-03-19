package com.lqs.scaffold.config;

import com.google.common.collect.Lists;
import com.lqs.scaffold.log.LogClientHttpRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * http 请求工具注入
 * @author lqs
 */
@Configuration
public class RestTemplateConfig {

    /*@Bean(name="restTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory){
        return new RestTemplate(requestFactory);
    }*/

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
        restTemplate.setInterceptors(Lists.newArrayList(
                new LogClientHttpRequestInterceptor()));
        //BufferingClientHttpRequestFactory  此处替换为BufferingClientHttpRequestFactory
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));
        return restTemplate;
    }
}
