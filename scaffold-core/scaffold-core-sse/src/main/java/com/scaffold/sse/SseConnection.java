package com.scaffold.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * 单个 SSE 物理连接。
 *
 * <p>每个连接持有独立的有界队列和一个虚拟线程。业务线程通过 {@link #offer(SseOutboundEvent)}
 * 非阻塞提交消息；虚拟线程逐条取出并交给发送器，从而保证单连接有序且不同连接相互隔离。</p>
 */
final class SseConnection {

    private final String id;
    private final String userId;
    private final Set<String> roomIds;
    private final SseEmitter emitter;
    private final BlockingQueue<SseOutboundEvent> queue;
    private final BiConsumer<SseConnection, SseOutboundEvent> dispatcher;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private volatile Thread worker;

    SseConnection(String id, String userId, Set<String> roomIds, SseEmitter emitter,
                  int queueCapacity, BiConsumer<SseConnection, SseOutboundEvent> dispatcher) {
        this.id = id;
        this.userId = userId;
        this.roomIds = roomIds;
        this.emitter = emitter;
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.dispatcher = dispatcher;
    }

    /** 启动本连接唯一的虚拟发送线程。该方法只能在连接完成注册后调用一次。 */
    void start() {
        worker = Thread.ofVirtual().name("sse-" + id).start(this::sendLoop);
    }

    /**
     * 尝试将事件加入队列，不进行等待。
     *
     * @return 连接仍有效且队列有空间时返回 {@code true}
     */
    boolean offer(SseOutboundEvent event) {
        return active.get() && queue.offer(event);
    }

    /** 幂等关闭连接发送线程并丢弃尚未发送的积压事件。 */
    void shutdown() {
        if (active.compareAndSet(true, false)) {
            Thread currentWorker = worker;
            if (currentWorker != null) currentWorker.interrupt();
        }
        queue.clear();
    }

    private void sendLoop() {
        try {
            while (active.get()) dispatcher.accept(this, queue.take());
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    String id() { return id; }
    String userId() { return userId; }
    Set<String> roomIds() { return roomIds; }
    SseEmitter emitter() { return emitter; }
}
