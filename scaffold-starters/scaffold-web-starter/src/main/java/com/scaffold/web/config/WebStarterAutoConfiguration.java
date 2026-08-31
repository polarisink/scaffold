package com.scaffold.web.config;

import tools.jackson.databind.json.JsonMapper;
import com.scaffold.base.util.JsonUtil;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

@AutoConfiguration(before = JacksonAutoConfiguration.class)
@Import({WebConfig.class, GlobalExceptionHandler.class})
@ImportRuntimeHints(BizLogRuntimeHints.class)
@EnableConfigurationProperties(ScaffoldWebProperties.class)
public class WebStarterAutoConfiguration {

    @Bean
    public static BeanFactoryPostProcessor bizLogAotBeanFactoryPostProcessor() {
        return new BizLogAotBeanFactoryPostProcessor();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "jsonMapper")
    public JsonMapper jsonMapper() {
        return JsonUtil.jsonMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "scaffold.web.request-log", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RequestLogFilter requestLogFilter(ScaffoldWebProperties scaffoldWebProperties) {
        return new RequestLogFilter(scaffoldWebProperties);
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
