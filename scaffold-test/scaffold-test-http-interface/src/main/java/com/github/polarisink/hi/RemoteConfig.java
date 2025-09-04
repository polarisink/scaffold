package com.github.polarisink.hi;

import com.github.polarisink.hi.api.RestApi;
import com.github.polarisink.hi.api.RetrofitApi;
import com.github.polarisink.hi.api.WebApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Configuration
public class RemoteConfig {

    String baseUrl = "http://localhost:8080";

    @Bean
    public RetrofitApi remoteApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // 关键：优先添加标量转换器
                .addConverterFactory(ScalarsConverterFactory.create())
                // 其他转换器（如 JSON）应放在后面
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitApi.class);
    }

    @Bean
    public RestApi restApi() {
        RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(RestApi.class);
    }

    @Bean
    public WebApi webApi() {
        WebClient restClient = WebClient.builder().baseUrl(baseUrl).build();
        WebClientAdapter adapter = WebClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(WebApi.class);
    }
}
