package com.scaffold.socket.util;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * socket发送消息用工具类
 *
 * @author machenike
 */
@Component
@RequiredArgsConstructor
public class WsManager {
    private final SocketIOServer server;


    /**
     * 发送消息 指定客户端
     *
     * @param clientId 客户端id
     * @param message  消息
     */
    public void sendByClientId(UUID clientId, String event, Object message) {
        server.getClient(clientId).sendEvent(event, message);
    }


    /**
     * 通过房间号发送消息
     *
     * @param roomId  房间号
     * @param event   事件
     * @param message 消息
     */
    public void sendByRoomId(String roomId, String event, Object message) {
        server.getRoomOperations(roomId).sendEvent(event, message);
    }
}
