package com.scaffold.sse;

import java.time.Instant;
import java.util.UUID;

/**
 * 与具体消息中间件无关的 SSE 业务消息。
 * Redis、Kafka 等实现只需序列化并传输该模型。
 */
public record SseMessage(
        String messageId,
        TargetType targetType,
        String targetId,
        String eventName,
        Object data,
        Instant createdAt) {

    public static SseMessage toUser(String userId, String eventName, Object data) {
        return create(TargetType.USER, userId, eventName, data);
    }

    public static SseMessage toRoom(String roomId, String eventName, Object data) {
        return create(TargetType.ROOM, roomId, eventName, data);
    }

    public static SseMessage broadcast(String eventName, Object data) {
        return create(TargetType.BROADCAST, null, eventName, data);
    }

    private static SseMessage create(TargetType type, String targetId, String eventName, Object data) {
        return new SseMessage(UUID.randomUUID().toString(), type, targetId, eventName, data, Instant.now());
    }

    public enum TargetType {
        USER, ROOM, BROADCAST
    }
}
