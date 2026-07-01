package com.scaffold.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class UserHttpInterfaceConfig {


    /**
     * 构建远程服务接口实例
     */
    @Bean
    public RemoteService userHttpInterface(RestClient.Builder loadBalancedRestClientBuilder) {
        RestClient restClient = loadBalancedRestClientBuilder
                // 指定服务提供方的应用名称（必须与其 spring.application.name 一致）
                .baseUrl("http://cloud-provider").build();

        // 创建 HttpServiceProxyFactory 工厂，用于生成接口代理对象
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

        // 生成并返回 UserHttpInterface 代理对象
        return factory.createClient(RemoteService.class);
    }
}
