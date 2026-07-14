package com.scaffold.sse;

import java.time.Instant;
import java.util.Set;

/** 客户端建立连接后收到的首个 {@code connected} 事件数据。 */
public record SseConnectedMessage(
        String connectionId,
        String userId,
        Set<String> roomIds,
        Instant connectedAt) {
}
