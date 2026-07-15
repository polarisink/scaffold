package com.scaffold.sse;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/** SSE 核心自动配置。 */
@AutoConfiguration
@EnableConfigurationProperties(SseProperties.class)
public class SseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    SseConnectionRepository sseConnectionRepository() {
        return new InMemorySseConnectionRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    SseLocalDispatcher sseLocalDispatcher(SseConnectionRepository repository, SseProperties properties) {
        return new SseLocalDispatcher(repository, properties.queueCapacity());
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    SseConnectionManager sseConnectionManager(SseConnectionRepository repository,
                                              SseLocalDispatcher dispatcher,
                                              SseMessageBroker broker,
                                              SseProperties properties) {
        return new SseConnectionManager(
                repository,
                dispatcher,
                broker,
                properties.queueCapacity(),
                properties.connectionTimeout().toMillis());
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    SseHeartbeatManager sseHeartbeatManager(SseConnectionManager manager, SseProperties properties) {
        return new SseHeartbeatManager(manager, properties.heartbeatInterval());
    }
}
