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
}
