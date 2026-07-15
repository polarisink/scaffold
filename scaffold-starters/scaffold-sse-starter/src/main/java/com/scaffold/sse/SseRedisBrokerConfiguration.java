package com.scaffold.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;

/** Redis Pub/Sub Broker 与本节点消费监听器配置。 */
@AutoConfiguration(after = SseAutoConfiguration.class)
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "scaffold.sse", name = "broker", havingValue = "redis")
public class SseRedisBrokerConfiguration {

    @Bean
    SseMessageBroker redisSseMessageBroker(StringRedisTemplate redisTemplate,
                                           ObjectMapper objectMapper,
                                           SseProperties properties) {
        return new RedisSseMessageBroker(redisTemplate, objectMapper, properties.redis().channel());
    }

    @Bean
    RedisMessageListenerContainer sseRedisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper,
            SseLocalDispatcher dispatcher,
            SseProperties properties) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener((message, pattern) -> {
            try {
                SseMessage sseMessage = objectMapper.readValue(
                        new String(message.getBody(), StandardCharsets.UTF_8), SseMessage.class);
                dispatcher.dispatch(sseMessage);
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to consume Redis SSE message", ex);
            }
        }, new ChannelTopic(properties.redis().channel()));
        return container;
    }
}
