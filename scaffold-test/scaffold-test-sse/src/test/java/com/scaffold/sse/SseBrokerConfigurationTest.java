package com.scaffold.sse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class SseBrokerConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SseBrokerConfiguration.class, DispatcherConfiguration.class);

    @Test
    void providesLocalBrokerByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SseMessageBroker.class);
            assertThat(context.getBean(SseMessageBroker.class)).isInstanceOf(LocalSseMessageBroker.class);
        });
    }

    @Test
    void backsOffWhenCustomBrokerIsProvided() {
        contextRunner.withPropertyValues("scaffold.sse.broker=kafka")
                .withUserConfiguration(CustomBrokerConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(SseMessageBroker.class);
            assertThat(context.getBean(SseMessageBroker.class)).isSameAs(context.getBean("customBroker"));
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class DispatcherConfiguration {
        @Bean
        SseConnectionRepository repository() {
            return new InMemorySseConnectionRepository();
        }

        @Bean
        SseLocalDispatcher dispatcher(SseConnectionRepository repository) {
            return new SseLocalDispatcher(repository, 10);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomBrokerConfiguration {
        @Bean
        SseMessageBroker customBroker() {
            return message -> SseSendResult.accepted(message.messageId());
        }
    }
}
