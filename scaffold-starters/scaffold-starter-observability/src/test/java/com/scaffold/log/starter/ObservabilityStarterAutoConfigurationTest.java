package com.scaffold.log.starter;

import com.scaffold.log.LogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ObservabilityStarterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ObservabilityStarterAutoConfiguration.class));

    @Test
    void shouldRegisterLogAspect() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(LogAspect.class));
    }
}
