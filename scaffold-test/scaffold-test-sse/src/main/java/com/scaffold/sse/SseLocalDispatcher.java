package com.scaffold.sse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;

/**
 * 将消息非阻塞地投递到当前节点的连接队列，并执行最终网络写入。
 * Broker 消费者应调用本类，而不是再次调用 Broker，避免消息循环发布。
 */
@Component
public final class SseLocalDispatcher {

    private final SseConnectionRepository repository;
    private final int queueCapacity;

    SseLocalDispatcher(SseConnectionRepository repository,
                       @Value("${scaffold.sse.queue-capacity:100}") int queueCapacity) {
        this.repository = repository;
        this.queueCapacity = queueCapacity;
    }

    /**
     * 将来自本地、Redis 或 Kafka 消费者的消息投递给当前节点连接。
     * 外部消息消费者必须调用此方法，不应再次调用 Broker 发布消息。
     */
    public int dispatch(SseMessage message) {
        Collection<SseConnection> targets = switch (message.targetType()) {
            case USER -> repository.findByUserId(message.targetId());
            case ROOM -> repository.findByRoomId(message.targetId());
            case BROADCAST -> repository.findAll();
        };
        return enqueue(targets, SseOutboundEvent.message(message.messageId(), message.eventName(), message.data()));
    }

    void heartbeat() {
        enqueue(repository.findAll(), SseOutboundEvent.heartbeat());
    }

    /** 将连接建立事件等节点内部消息投递到单个连接。 */
    boolean dispatchToConnection(SseConnection connection, SseOutboundEvent event) {
        return enqueue(java.util.List.of(connection), event) == 1;
    }

    void write(SseConnection connection, SseOutboundEvent event) {
        try {
            if (event.isHeartbeat()) {
                connection.emitter().send(SseEmitter.event().comment("heartbeat"));
            } else {
                connection.emitter().send(SseEmitter.event().id(event.id()).name(event.name()).data(event.data()));
            }
        } catch (IOException | RuntimeException ex) {
            close(connection.id(), ex);
        }
    }

    private int enqueue(Collection<SseConnection> connections, SseOutboundEvent event) {
        int accepted = 0;
        for (SseConnection connection : connections) {
            if (connection.offer(event)) {
                accepted++;
            } else {
                close(connection.id(), new SseSlowClientException(connection.id(), queueCapacity));
            }
        }
        return accepted;
    }

    private void close(String connectionId, Throwable error) {
        SseConnection connection = repository.remove(connectionId);
        if (connection != null) connection.emitter().completeWithError(error);
    }
}
