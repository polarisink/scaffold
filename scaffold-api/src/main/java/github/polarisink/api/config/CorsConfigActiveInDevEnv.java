package github.polarisink.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 一般dev环境通过域名访问，需要配置跨域，在test和prod等有网关配置，就不再需要配置跨域
 * @author lqs
 * @date 2023/4/2
 */
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev", matchIfMissing = true)
@Component
public class CorsConfigActiveInDevEnv {

  @Bean
  public CorsWebFilter corsWebFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 配置跨域
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    // 允许哪个请求头
    corsConfiguration.addAllowedHeader("*");
    // 允许哪个方法进行跨域
    corsConfiguration.addAllowedMethod("*");
    // 允许哪个请求来源进行跨域
    // corsConfiguration.addAllowedOrigin("*");
    corsConfiguration.addAllowedOriginPattern("*");
    // 是否允许携带cookie进行跨域
    corsConfiguration.setAllowCredentials(true);
    source.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsWebFilter(source);
  }
}
