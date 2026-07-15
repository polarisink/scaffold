package com.scaffold.sse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

/** 使用 Redis Pub/Sub 发布 SSE 消息。 */
final class RedisSseMessageBroker implements SseMessageBroker {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final String channel;

    RedisSseMessageBroker(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, String channel) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.channel = channel;
    }

    @Override
    public SseSendResult publish(SseMessage message) {
        try {
            redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(message));
            return SseSendResult.accepted(message.messageId());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize SSE message " + message.messageId(), ex);
        }
    }
}
