package polarisink.github.scaffold.config;

import polarisink.github.scaffold.bean.properties.SwaggerProperties;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger配置类
 *
 * @author aries
 * @date 2022/5/12
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class Swagger3Config {

  private final SwaggerProperties properties;

  @Bean
  public Docket createRestApi() {
    LOG.info("Enable Swagger3: {}", properties.getEnable());
    /*@formatter:off*/
    return new Docket(DocumentationType.OAS_30)
        .apiInfo(apiInfo())
        .enable(properties.getEnable())
        .select()
        .apis(RequestHandlerSelectors.withMethodAnnotation(Operation.class))
        //.apis(RequestHandlerSelectors.basePackage("package polarisink.github.scaffold.controller"))
        .paths(PathSelectors.any()).build()
        .host(properties.getServiceUrl());
    /*@formatter:on*/
  }

  /**
   * 构建 api文档的详细信息函数,注意这里的注解引用的是哪个
   */
  private ApiInfo apiInfo() {
    // 获取工程名称
    /*@formatter:off*/
    return new ApiInfoBuilder()
        .title(properties.getTitle())
        .contact(new Contact(properties.getAuthor(), properties.getUrl(), properties.getEmail()))
        .license(properties.getLicense())
        .version(properties.getVersion())
        .description(properties.getDescription())
        .build();
    /*@formatter:on*/
  }
}
