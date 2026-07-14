package com.scaffold.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** 基于并发 Map 的本节点连接仓库，同时维护用户和房间二级索引。 */
@Slf4j
@Component
final class InMemorySseConnectionRepository implements SseConnectionRepository {

    private final ConcurrentMap<String, SseConnection> connections = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> userConnections = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> roomConnections = new ConcurrentHashMap<>();

    @Override
    public void save(SseConnection connection) {
        connections.put(connection.id(), connection);
        addIndex(userConnections, connection.userId(), connection.id());
        connection.roomIds().forEach(roomId -> addIndex(roomConnections, roomId, connection.id()));
    }

    @Override
    public SseConnection remove(String connectionId) {
        SseConnection connection = connections.remove(connectionId);
        if (connection == null) return null;
        removeIndex(userConnections, connection.userId(), connectionId);
        connection.roomIds().forEach(roomId -> removeIndex(roomConnections, roomId, connectionId));
        connection.shutdown();
        log.info("SSE disconnected: connectionId={}, userId={}", connectionId, connection.userId());
        return connection;
    }

    @Override
    public Collection<SseConnection> findByUserId(String userId) {
        return findByIndex(userConnections, userId);
    }

    @Override
    public Collection<SseConnection> findByRoomId(String roomId) {
        return findByIndex(roomConnections, roomId);
    }

    @Override
    public Collection<SseConnection> findAll() {
        return Set.copyOf(connections.values());
    }

    @Override
    public int connectionCount() {
        return connections.size();
    }

    @Override
    public int userCount() {
        return userConnections.size();
    }

    private Collection<SseConnection> findByIndex(ConcurrentMap<String, Set<String>> index, String key) {
        Set<String> ids = index.get(key);
        if (ids == null) return Set.of();
        return ids.stream().map(connections::get).filter(java.util.Objects::nonNull).toList();
    }

    private static void addIndex(ConcurrentMap<String, Set<String>> index, String key, String connectionId) {
        index.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet()).add(connectionId);
    }

    private static void removeIndex(ConcurrentMap<String, Set<String>> index, String key, String connectionId) {
        index.computeIfPresent(key, (ignored, ids) -> {
            ids.remove(connectionId);
            return ids.isEmpty() ? null : ids;
        });
    }
}
