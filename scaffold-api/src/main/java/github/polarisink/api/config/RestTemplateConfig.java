package github.polarisink.api.config;

import github.polarisink.api.core.RestClientHttpInterceptor;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


/**
 * http 请求工具注入
 *
 * @author hzsk
 */
@Configuration
public class RestTemplateConfig {

  @Value("${okhttp.connect-timeout}")
  private Integer connectTimeout;

  @Value("${okhttp.read-timeout}")
  private Integer readTimeout;

  @Value("${okhttp.write-timeout}")
  private Integer writeTimeout;

  @Value("${okhttp.max-idle-connections}")
  private Integer maxIdleConnections;

  @Value("${okhttp.keep-alive-duration}")
  private Long keepAliveDuration;


  @Bean
  public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
    ClientHttpRequestFactory factory = httpRequestFactory();
    RestTemplate restTemplate = new RestTemplate(factory);
    /**
     * StringHttpMessageConverter 默认使用ISO-8859-1编码，此处修改为UTF-8
     */
    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
    messageConverters.stream().filter(converter -> converter instanceof StringHttpMessageConverter).forEachOrdered(
        converter -> ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8));
    //Interceptors 添加写的 Interceptors
    restTemplate.setInterceptors(Collections.singletonList(new RestClientHttpInterceptor()));
    //BufferingClientHttpRequestFactory  此处替换为BufferingClientHttpRequestFactory
    restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));
    return restTemplate;
  }

  @Bean
  public ClientHttpRequestFactory httpRequestFactory() {
    return new OkHttp3ClientHttpRequestFactory(okHttpConfigClient());
  }

  /*@formatter:off*/
  public OkHttpClient okHttpConfigClient() {
    return new OkHttpClient().newBuilder()
        .connectionPool(pool())
        .connectTimeout(connectTimeout, TimeUnit.SECONDS)
        .readTimeout(readTimeout, TimeUnit.SECONDS)
        .writeTimeout(writeTimeout, TimeUnit.SECONDS)
        .hostnameVerifier((hostname, session) -> true)
        /*.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
        .addInterceptor()*/.build();
    /*@formatter:on*/
  }

  public ConnectionPool pool() {
    return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
  }

}