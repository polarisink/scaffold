package com.scaffold;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * socket处理拦截器
 * todo 持久化在线消息
 *
 * @author machenike
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WsServer implements ApplicationRunner, DisposableBean {

    /**
     * 连接数
     */
    private final SocketIOServer server;

    /**
     * 客户端连上socket服务器时执行此事件
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        String trainId = client.getHandshakeData().getSingleUrlParam("trainId");
        client.joinRoom(trainId);
    }


    /**
     * 客户端断开socket服务器时执行此事件
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String trainId = client.getHandshakeData().getSingleUrlParam("trainId");
        client.leaveRoom(trainId);
    }

    /**
     * @param client
     */
    @OnEvent(value = "message")
    public void onMessage(SocketIOClient client, AckRequest request, Object data) {
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        server.start();

    }
}
