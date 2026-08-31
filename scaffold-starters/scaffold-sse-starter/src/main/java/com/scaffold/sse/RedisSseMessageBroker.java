package com.scaffold.sse;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

/** 使用 Redis Pub/Sub 发布 SSE 消息。 */
final class RedisSseMessageBroker implements SseMessageBroker {

    private final StringRedisTemplate redisTemplate;
    private final JsonMapper jsonMapper;
    private final String channel;

    RedisSseMessageBroker(StringRedisTemplate redisTemplate, JsonMapper jsonMapper, String channel) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
        this.channel = channel;
    }

    @Override
    public SseSendResult publish(SseMessage message) {
        redisTemplate.convertAndSend(channel, jsonMapper.writeValueAsString(message));
        return SseSendResult.accepted(message.messageId());
    }
}
