package github.polarisink.api.config;

import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lqs mvc配置
 */
@Slf4j
@Configuration
public class MvcConfig implements WebMvcConfigurer {


  @Value("${spring.profiles.active}")
  String active;

  /**
   * jar不需要配置跨域,通过gateway访问,有全局跨域,dev通过ip访问,需要配置跨域
   *
   * @return
   */
  public boolean loadCorsConfig() {
    boolean notProd = !Objects.equals(active, "prod");
    LOG.info("Load Cors Config : {}", notProd);
    return notProd;
  }

  /**
   * 跨域设置 使用gateway代理的时候不需要配置跨域
   */
  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {
    if (loadCorsConfig()) {
      /*@formatter:off*/
      corsRegistry
          .addMapping("/**")
          .allowedOrigins("*")
          .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
          .allowCredentials(true)
          .maxAge(3600)
          .allowedHeaders("*");
      /*@formatter:on*/
    }
  }


  /**
   * ResponseBodyAdvice中遇到String会报错. 因为在所有的HttpMessageConverter实例集合中,StringHttpMessageConverter要比其它的Converter排得靠前一些.
   * 我们需要将处理Object类型的HttpMessageConverter放得靠前一些
   *
   * @param converters
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(0, new MappingJackson2HttpMessageConverter());
  }
}
