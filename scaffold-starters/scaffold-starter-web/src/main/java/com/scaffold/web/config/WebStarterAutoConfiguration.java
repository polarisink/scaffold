package com.scaffold.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({WebConfig.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(WebProperties.class)
public class WebStarterAutoConfiguration {
}
