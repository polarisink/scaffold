package com.scaffold.sse;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/** 默认本地 Broker 配置。 */
@AutoConfiguration(after = SseAutoConfiguration.class)
@ConditionalOnProperty(prefix = "scaffold.sse", name = "broker", havingValue = "local", matchIfMissing = true)
public class SseLocalBrokerConfiguration {

    @Bean
    SseMessageBroker localSseMessageBroker(SseLocalDispatcher dispatcher) {
        return new LocalSseMessageBroker(dispatcher);
    }
}
