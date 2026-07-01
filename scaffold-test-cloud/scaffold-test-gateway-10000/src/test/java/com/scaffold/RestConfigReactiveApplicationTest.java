package com.scaffold;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class RestConfigReactiveApplicationTest {

    private final ReactiveWebApplicationContextRunner contextRunner =
            new ReactiveWebApplicationContextRunner()
                    .withUserConfiguration(RestConfig.class);

    @Test
    void shouldNotCreateBlockingRestClientBuilderInReactiveApplication() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean("loadBalancedRestClientBuilder");
        });
    }
}
