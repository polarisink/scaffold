package com.scaffold.sse;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * SSE 对外门面，负责建立连接、生命周期绑定、消息发布和在线统计。
 *
 * <p>连接数据交由 {@link SseConnectionRepository} 保存，本节点网络投递交由
 * {@link SseLocalDispatcher} 完成，业务消息交由 {@link SseMessageBroker} 发布。
 * 因此未来切换 Redis 或 Kafka 时，不需要修改该门面和业务调用方。</p>
 */
@Slf4j
@Component
public class SseConnectionManager {

    /** 默认连接超时时间：30 分钟。 */
    public static final long DEFAULT_TIMEOUT = 30 * 60 * 1000L;

    private final SseConnectionRepository repository;
    private final SseLocalDispatcher localDispatcher;
    private final SseMessageBroker messageBroker;
    private final int queueCapacity;

    SseConnectionManager(SseConnectionRepository repository,
                         SseLocalDispatcher localDispatcher,
                         SseMessageBroker messageBroker,
                         @Value("${scaffold.sse.queue-capacity:100}") int queueCapacity) {
        Assert.isTrue(queueCapacity > 0, "scaffold.sse.queue-capacity must be greater than 0");
        this.repository = repository;
        this.localDispatcher = localDispatcher;
        this.messageBroker = messageBroker;
        this.queueCapacity = queueCapacity;
    }

    /**
     * 创建一个 SSE 连接并加入指定房间。
     *
     * @param userId  当前登录用户 ID，生产环境应从可信登录上下文中取得
     * @param roomIds 初始加入的房间 ID，可以为空
     */
    public SseEmitter connect(String userId, Collection<String> roomIds) {
        return connect(userId, roomIds, DEFAULT_TIMEOUT);
    }

    /** 包级重载用于测试自定义超时时间。 */
    SseEmitter connect(String userId, Collection<String> roomIds, long timeout) {
        Assert.hasText(userId, "userId must not be blank");

        String connectionId = UUID.randomUUID().toString();
        Set<String> normalizedRooms = normalizeRooms(roomIds);
        SseEmitter emitter = createEmitter(timeout);
        SseConnection connection = new SseConnection(
                connectionId, userId, normalizedRooms, emitter, queueCapacity, localDispatcher::write);

        repository.save(connection);
        bindLifecycleCallbacks(connection);
        connection.start();

        SseMessage connected = new SseMessage(
                connectionId,
                SseMessage.TargetType.USER,
                userId,
                "connected",
                new SseConnectedMessage(connectionId, userId, normalizedRooms, Instant.now()),
                Instant.now());
        localDispatcher.dispatchToConnection(connection,
                SseOutboundEvent.message(connected.messageId(), connected.eventName(), connected.data()));
        log.info("SSE connected: connectionId={}, userId={}, roomIds={}", connectionId, userId, normalizedRooms);
        return emitter;
    }

    /** 为测试保留的 emitter 创建扩展点。 */
    protected SseEmitter createEmitter(long timeout) {
        return new SseEmitter(timeout);
    }

    /** 将用户定向消息提交给当前配置的 Broker。 */
    public SseSendResult sendToUser(String userId, String eventName, Object data) {
        Assert.hasText(userId, "userId must not be blank");
        Assert.hasText(eventName, "eventName must not be blank");
        return messageBroker.sendToUser(userId, eventName, data);
    }

    /** 将房间广播消息提交给当前配置的 Broker。 */
    public SseSendResult sendToRoom(String roomId, String eventName, Object data) {
        Assert.hasText(roomId, "roomId must not be blank");
        Assert.hasText(eventName, "eventName must not be blank");
        return messageBroker.sendToRoom(roomId, eventName, data);
    }

    /** 将全局广播消息提交给当前配置的 Broker。 */
    public SseSendResult broadcast(String eventName, Object data) {
        Assert.hasText(eventName, "eventName must not be blank");
        return messageBroker.broadcast(eventName, data);
    }

    /** 心跳只针对当前节点物理连接，不经过外部消息中间件。 */
    public void heartbeat() {
        localDispatcher.heartbeat();
    }

    public int onlineConnectionCount() {
        return repository.connectionCount();
    }

    public int onlineUserCount() {
        return repository.userCount();
    }

    /** 应用停止时关闭当前节点全部长连接和虚拟发送线程。 */
    @PreDestroy
    public void shutdown() {
        for (SseConnection connection : repository.findAll()) {
            SseConnection removed = repository.remove(connection.id());
            if (removed != null) removed.emitter().complete();
        }
    }

    private void bindLifecycleCallbacks(SseConnection connection) {
        String connectionId = connection.id();
        connection.emitter().onCompletion(() -> repository.remove(connectionId));
        connection.emitter().onTimeout(() -> repository.remove(connectionId));
        connection.emitter().onError(error -> repository.remove(connectionId));
    }

    private static Set<String> normalizeRooms(Collection<String> roomIds) {
        if (roomIds == null) return Set.of();
        Set<String> result = new HashSet<>();
        for (String roomId : roomIds) {
            if (roomId != null && !roomId.isBlank()) result.add(roomId.trim());
        }
        return Set.copyOf(result);
    }
}
