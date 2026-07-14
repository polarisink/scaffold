package com.scaffold.sse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** SSE Broker 装配入口；默认使用本地实现，通过配置显式切换其他消息中间件。 */
@Configuration(proxyBeanMethods = false)
class SseBrokerConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "scaffold.sse", name = "broker", havingValue = "local", matchIfMissing = true)
    SseMessageBroker localSseMessageBroker(SseLocalDispatcher dispatcher) {
        return new LocalSseMessageBroker(dispatcher);
    }
}
