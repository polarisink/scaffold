package com.scaffold.sse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class SseAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SseAutoConfiguration.class, SseLocalBrokerConfiguration.class));

    @Test
    void configuresLocalSseComponentsByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SseConnectionManager.class);
            assertThat(context).hasSingleBean(SseMessageBroker.class);
            assertThat(context.getBean(SseMessageBroker.class)).isInstanceOf(LocalSseMessageBroker.class);
        });
    }

    @Test
    void disablesLocalBrokerWhenAnotherProviderIsSelected() {
        contextRunner.withPropertyValues("scaffold.sse.broker=kafka")
                .withUserConfiguration(CustomBrokerConfiguration.class).run(context -> {
            assertThat(context).doesNotHaveBean(LocalSseMessageBroker.class);
            assertThat(context).hasSingleBean(SseConnectionManager.class);
            assertThat(context.getBean(SseMessageBroker.class)).isSameAs(context.getBean("customBroker"));
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomBrokerConfiguration {
        @Bean
        SseMessageBroker customBroker() {
            return message -> SseSendResult.accepted(message.messageId());
        }
    }
}
