package com.scaffold.sse;

/**
 * 客户端消费速度长期落后，导致该连接的有界发送队列已满。
 * 发生此异常时管理器会主动淘汰连接，以保护业务线程和服务端内存。
 */
public class SseSlowClientException extends IllegalStateException {

    public SseSlowClientException(String connectionId, int queueCapacity) {
        super("SSE connection " + connectionId + " exceeded queue capacity " + queueCapacity);
    }
}
