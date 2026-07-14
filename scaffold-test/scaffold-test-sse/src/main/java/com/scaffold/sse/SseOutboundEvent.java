package com.scaffold.sse;

/**
 * 服务端内部发送队列中的事件模型。
 * 心跳使用 SSE 注释发送，因此不需要事件 ID、名称和数据。
 */
record SseOutboundEvent(String id, String name, Object data, boolean isHeartbeat) {

    static SseOutboundEvent message(String id, String name, Object data) {
        return new SseOutboundEvent(id, name, data, false);
    }

    static SseOutboundEvent heartbeat() {
        return new SseOutboundEvent(null, null, null, true);
    }
}
