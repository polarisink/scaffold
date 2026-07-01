package com.scaffold.config;

import com.scaffold.remote.OrderTransactionClient;
import com.scaffold.remote.ProviderTransactionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import static com.scaffold.RestConfig.createClient;

@Configuration
public class TransactionClientConfiguration {

    @Bean
    public ProviderTransactionClient providerTransactionClient(
            RestClient.Builder loadBalancedRestClientBuilder) {
        return createClient(
                loadBalancedRestClientBuilder,
                "http://cloud-provider-10081",
                ProviderTransactionClient.class);
    }

    @Bean
    public OrderTransactionClient orderTransactionClient(RestClient.Builder loadBalancedRestClientBuilder) {
        return createClient(
                loadBalancedRestClientBuilder,
                "http://cloud-order-10083",
                OrderTransactionClient.class);
    }

}
