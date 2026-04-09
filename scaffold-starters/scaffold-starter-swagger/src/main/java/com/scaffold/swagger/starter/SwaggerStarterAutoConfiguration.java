package com.scaffold.swagger.starter;

import com.scaffold.swagger.config.DocumentRunner;
import com.scaffold.swagger.config.OpenApiConfig;
import com.scaffold.swagger.properties.SwaggerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({OpenApiConfig.class, DocumentRunner.class})
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerStarterAutoConfiguration {
}
