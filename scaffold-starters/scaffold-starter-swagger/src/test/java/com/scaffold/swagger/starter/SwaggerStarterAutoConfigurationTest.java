package com.scaffold.swagger.starter;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerStarterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SwaggerStarterAutoConfiguration.class));

    @Test
    void shouldStayDisabledByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(OpenAPI.class));
    }

    @Test
    void shouldCreateOpenApiBeanWhenEnabled() {
        contextRunner
                .withPropertyValues(
                        "swagger.enabled=true",
                        "swagger.title=Scaffold API",
                        "swagger.contact.name=Scaffold Team"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(OpenAPI.class);
                    OpenAPI openAPI = context.getBean(OpenAPI.class);
                    assertThat(openAPI.getInfo().getTitle()).isEqualTo("Scaffold API");
                    assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Scaffold Team");
                });
    }
}
