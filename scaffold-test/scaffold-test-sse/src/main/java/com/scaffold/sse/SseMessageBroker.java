package com.scaffold.sse;

/**
 * SSE 业务消息发布接口。
 *
 * <p>业务层只依赖该接口。单机模式直接投递本地连接；Redis、Kafka 等实现负责把
 * {@link SseMessage} 发布到中间件，再由每个应用节点的消费者投递到本地连接。</p>
 */
public interface SseMessageBroker {

    SseSendResult publish(SseMessage message);

    default SseSendResult sendToUser(String userId, String eventName, Object data) {
        return publish(SseMessage.toUser(userId, eventName, data));
    }

    default SseSendResult sendToRoom(String roomId, String eventName, Object data) {
        return publish(SseMessage.toRoom(roomId, eventName, data));
    }

    default SseSendResult broadcast(String eventName, Object data) {
        return publish(SseMessage.broadcast(eventName, data));
    }
}
