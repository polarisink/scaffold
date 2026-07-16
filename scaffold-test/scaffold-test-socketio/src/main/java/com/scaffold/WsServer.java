package com.scaffold;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * socket处理拦截器
 * todo 持久化在线消息
 *
 * @author machenike
 */
@Slf4j
@Component
public class WsServer {

    /**
     * 客户端连上socket服务器时执行此事件
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        String clientId = client.getHandshakeData().getSingleUrlParam("clientId");
        if (clientId == null || clientId.isBlank()) {
            log.warn("Rejecting Socket.IO connection without clientId: {}", client.getSessionId());
            client.disconnect();
            return;
        }
        client.joinRoom(clientId);
        log.info("Socket.IO client connected: sessionId={}, clientId={}", client.getSessionId(), clientId);
    }


    /**
     * 客户端断开socket服务器时执行此事件
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String clientId = client.getHandshakeData().getSingleUrlParam("clientId");
        if (clientId != null && !clientId.isBlank()) {
            client.leaveRoom(clientId);
        }
        log.info("Socket.IO client disconnected: sessionId={}, clientId={}", client.getSessionId(), clientId);
    }

    /**
     * @param client
     */
    @OnEvent(value = "message")
    public void onMessage(SocketIOClient client, AckRequest request, Object data) {
        client.sendEvent("message", data);
        if (request.isAckRequested()) {
            request.sendAckData("ok");
        }
    }

}
