package com.scaffold.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SseMessageBrokerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void publishesSerializedMessageToRedisChannel() {
        StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
        RedisSseMessageBroker broker = new RedisSseMessageBroker(redisTemplate, objectMapper, "test-channel");

        SseSendResult result = broker.sendToUser("user-1", "notice", "hello");

        assertThat(result.accepted()).isTrue();
        assertThat(result.enqueuedConnections()).isNull();
        verify(redisTemplate).convertAndSend(eq("test-channel"), startsWith("{\"messageId\""));
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishesSerializedMessageToKafkaTopic() {
        KafkaTemplate<Object, Object> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaSseMessageBroker broker = new KafkaSseMessageBroker(kafkaTemplate, objectMapper, "test-topic");

        SseSendResult result = broker.sendToRoom("room-1", "chat", "hello");

        assertThat(result.accepted()).isTrue();
        verify(kafkaTemplate).send(eq("test-topic"), eq(result.messageId()), startsWith("{\"messageId\""));
    }

    @Test
    void appliesSafeDefaults() {
        SseProperties properties = new SseProperties(null, null, null, 0, null, null);

        assertThat(properties.broker()).isEqualTo(SseProperties.Broker.LOCAL);
        assertThat(properties.queueCapacity()).isEqualTo(100);
        assertThat(properties.redis().channel()).isEqualTo("scaffold:sse:messages");
        assertThat(properties.kafka().topic()).isEqualTo("scaffold-sse-messages");
        assertThat(properties.kafka().groupId()).startsWith("scaffold-sse-");
    }
}
