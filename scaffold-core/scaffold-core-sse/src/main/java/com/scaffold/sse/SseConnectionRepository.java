package com.scaffold.sse;

import java.util.Collection;

/** 当前应用节点的物理 SSE 连接仓库。真实 {@code SseEmitter} 始终只能保存在本机内存。 */
interface SseConnectionRepository {

    void save(SseConnection connection);

    SseConnection remove(String connectionId);

    SseConnection findById(String connectionId);

    Collection<SseConnection> findByUserId(String userId);

    Collection<SseConnection> findByRoomId(String roomId);

    Collection<SseConnection> findAll();

    int connectionCount();

    int userCount();
}
