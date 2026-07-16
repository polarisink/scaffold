package com.scaffold.redis.messaging;

import com.scaffold.redis.core.RedisMessageQueueRegister;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RedisMessagingAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisMessagingAutoConfiguration.class))
            .withBean(RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class));

    @Test
    void shouldStayDisabledByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(RedisMessageQueueRegister.class));
    }

    @Test
    void shouldCreateMessagingInfrastructureWhenEnabled() {
        contextRunner.withPropertyValues("scaffold.redis.messaging.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(RedisMessageQueueRegister.class));
    }
}
