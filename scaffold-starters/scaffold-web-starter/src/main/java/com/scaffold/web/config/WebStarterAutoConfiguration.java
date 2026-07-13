package com.scaffold.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

@AutoConfiguration
@Import({WebConfig.class, GlobalExceptionHandler.class})
@EnableConfigurationProperties(WebProperties.class)
public class WebStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scaffold.web.request-log", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RequestLogFilter requestLogFilter(WebProperties webProperties) {
        return new RequestLogFilter(webProperties);
    }

    @Bean
    @ConditionalOnBean(RequestLogFilter.class)
    public FilterRegistrationBean<RequestLogFilter> requestLogFilterRegistration(RequestLogFilter filter) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        return registration;
    }
}
