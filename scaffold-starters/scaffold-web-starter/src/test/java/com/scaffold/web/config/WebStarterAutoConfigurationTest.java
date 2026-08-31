package com.scaffold.web.config;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.codec.autoconfigure.CodecsAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class WebStarterAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebStarterAutoConfiguration.class));

    @Test
    void shouldRegisterDefaultWebBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(WebConfig.class);
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(ScaffoldWebProperties.class);
            assertThat(context).hasSingleBean(RequestLogFilter.class);
            assertThat(context).hasSingleBean(FilterRegistrationBean.class);
            assertThat(context).hasSingleBean(JsonMapper.class);
        });
    }

    @Test
    void shouldExposeSingleJsonMapperWithBootJacksonAndReactiveCodecs() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        WebStarterAutoConfiguration.class,
                        JacksonAutoConfiguration.class,
                        CodecsAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(JsonMapper.class);
                    assertThat(context).hasBean("jsonMapper");
                });
    }

    @Test
    void shouldStillCreatePrimaryWebJsonMapperWhenSpecializedMapperExists() {
        contextRunner.withUserConfiguration(SpecializedMapperConfiguration.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasBean("jsonMapper");
                    assertThat(context).hasBean("redisJsonMapper");
                    assertThat(context.getBean(JsonMapper.class))
                            .isSameAs(context.getBean("jsonMapper", JsonMapper.class));
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class SpecializedMapperConfiguration {

        @Bean("redisJsonMapper")
        JsonMapper redisJsonMapper() {
            return JsonMapper.builder().build();
        }
    }

    @Test
    void shouldAllowRequestLoggingToBeDisabled() {
        contextRunner
                .withPropertyValues("scaffold.web.request-log.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(RequestLogFilter.class));
    }

    @Test
    void shouldBindRequestLogProperties() {
        contextRunner
                .withPropertyValues(
                        "scaffold.web.request-log.slow-threshold-millis=500",
                        "scaffold.web.request-log.max-payload-length=2048",
                        "scaffold.web.request-log.exclude-path-patterns[0]=/actuator/**"
                )
                .run(context -> {
                    ScaffoldWebProperties.RequestLog requestLog = context.getBean(ScaffoldWebProperties.class).requestLog();
                    assertThat(requestLog.getSlowThresholdMillis()).isEqualTo(500);
                    assertThat(requestLog.getMaxPayloadLength()).isEqualTo(2048);
                    assertThat(requestLog.getExcludePathPatterns()).containsExactly("/actuator/**");
                });
    }

    @Test
    void shouldBindCorsProperties() {
        contextRunner
                .withPropertyValues(
                        "scaffold.web.cors.enabled=true",
                        "scaffold.web.cors.path-pattern=/api/**",
                        "scaffold.web.cors.allowed-origin-patterns[0]=https://*.example.com"
                )
                .run(context -> {
                    ScaffoldWebProperties properties = context.getBean(ScaffoldWebProperties.class);
                    assertThat(properties.cors().isEnabled()).isTrue();
                    assertThat(properties.cors().getPathPattern()).isEqualTo("/api/**");
                    assertThat(properties.cors().getAllowedOriginPatterns()).contains("https://*.example.com");
                });
    }

    @Test
    void shouldBindResponseProperties() {
        contextRunner
                .withPropertyValues(
                        "scaffold.web.response.server-error-message=自定义错误消息",
                        "scaffold.web.response.ignored-class-name-prefixes[0]=com.example.raw",
                        "scaffold.web.response.raw-body-path-patterns[0]=/internal/**"
                )
                .run(context -> {
                    ScaffoldWebProperties.Response response = context.getBean(ScaffoldWebProperties.class).response();
                    assertThat(response.getServerErrorMessage()).isEqualTo("自定义错误消息");
                    assertThat(response.getIgnoredClassNamePrefixes()).containsExactly("com.example.raw");
                    assertThat(response.getRawBodyPathPatterns()).containsExactly("/internal/**");
                });
    }

    @Test
    void shouldKeepResponseDefaults() {
        contextRunner.run(context -> {
            ScaffoldWebProperties.Response response = context.getBean(ScaffoldWebProperties.class).response();
            assertThat(response.getServerErrorMessage()).isEqualTo("服务器或网络开小差了，请联系管理员");
            assertThat(response.getIgnoredClassNamePrefixes())
                    .contains("org.springdoc.webmvc", "org.springframework.boot.actuate", "de.codecentric.boot.admin");
            assertThat(response.getRawBodyPathPatterns()).contains("/actuator", "/actuator/**");
        });
    }
}
