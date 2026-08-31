package com.scaffold.sse;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.kafka.core.KafkaTemplate;

/** 使用 Kafka 发布 SSE 消息。 */
final class KafkaSseMessageBroker implements SseMessageBroker {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final JsonMapper jsonMapper;
    private final String topic;

    KafkaSseMessageBroker(KafkaTemplate<Object, Object> kafkaTemplate, JsonMapper jsonMapper, String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
        this.topic = topic;
    }

    @Override
    public SseSendResult publish(SseMessage message) {
        kafkaTemplate.send(topic, message.messageId(), jsonMapper.writeValueAsString(message));
        return SseSendResult.accepted(message.messageId());
    }
}
