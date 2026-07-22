package com.scaffold.nativetest;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通过api注册listener，通过注解对native打包不友好
 */
@Slf4j
@Component
public class SocketIoNativeTestHandler {

    public SocketIoNativeTestHandler(SocketIOServer server) {
        server.addConnectListener(this::onConnect);
        server.addDisconnectListener(this::onDisconnect);
        server.addEventListener("native-health", Object.class, health());
        server.addEventListener("native-echo", String.class, echo());
    }

    private void onConnect(SocketIOClient client) {
        String clientId = client.getHandshakeData().getSingleUrlParam("clientId");
        log.info("Native test Socket.IO connected: sessionId={}, clientId={}", client.getSessionId(), clientId);
    }

    private void onDisconnect(SocketIOClient client) {
        log.info("Native test Socket.IO disconnected: sessionId={}", client.getSessionId());
    }

    private void health(SocketIOClient client, AckRequest ackRequest) {
        String response = "Socket.IO native handler ready";
        client.sendEvent("server-ready", response);
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(response);
        }
    }

    private DataListener<String> echo() {
        return (SocketIOClient client, String message, AckRequest ackRequest) -> {
            String response = "native echo: " + message;
            client.sendEvent("native-echo", response);
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData(response);
            }
        };
    }

    private DataListener<Object> health() {
        return (SocketIOClient client, Object ignored, AckRequest ackRequest) -> {
            String response = "Socket.IO native handler ready";
            client.sendEvent("server-ready", response);
            if (ackRequest.isAckRequested()) {
                ackRequest.sendAckData(response);
            }
        };
    }

}
