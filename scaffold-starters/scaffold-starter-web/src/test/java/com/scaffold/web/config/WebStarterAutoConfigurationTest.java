package com.scaffold.web.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class WebStarterAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(WebStarterAutoConfiguration.class));

    @Test
    void shouldRegisterDefaultWebBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(WebConfig.class);
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(WebProperties.class);
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
                    WebProperties properties = context.getBean(WebProperties.class);
                    assertThat(properties.getCors().isEnabled()).isTrue();
                    assertThat(properties.getCors().getPathPattern()).isEqualTo("/api/**");
                    assertThat(properties.getCors().getAllowedOriginPatterns()).contains("https://*.example.com");
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
                    WebProperties.Response response = context.getBean(WebProperties.class).getResponse();
                    assertThat(response.getServerErrorMessage()).isEqualTo("自定义错误消息");
                    assertThat(response.getIgnoredClassNamePrefixes()).containsExactly("com.example.raw");
                    assertThat(response.getRawBodyPathPatterns()).containsExactly("/internal/**");
                });
    }

    @Test
    void shouldKeepResponseDefaults() {
        contextRunner.run(context -> {
            WebProperties.Response response = context.getBean(WebProperties.class).getResponse();
            assertThat(response.getServerErrorMessage()).isEqualTo("服务器或网络开小差了，请联系管理员");
            assertThat(response.getIgnoredClassNamePrefixes())
                    .contains("org.springdoc.webmvc", "org.springframework.boot.actuate", "de.codecentric.boot.admin");
            assertThat(response.getRawBodyPathPatterns()).contains("/actuator", "/actuator/**");
        });
    }
}
