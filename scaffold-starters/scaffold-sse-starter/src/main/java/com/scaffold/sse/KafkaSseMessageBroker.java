package com.scaffold.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

/** 使用 Kafka 发布 SSE 消息。 */
final class KafkaSseMessageBroker implements SseMessageBroker {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    KafkaSseMessageBroker(KafkaTemplate<Object, Object> kafkaTemplate, ObjectMapper objectMapper, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @Override
    public SseSendResult publish(SseMessage message) {
        try {
            kafkaTemplate.send(topic, message.messageId(), objectMapper.writeValueAsString(message));
            return SseSendResult.accepted(message.messageId());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize SSE message " + message.messageId(), ex);
        }
    }
}
