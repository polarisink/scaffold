package com.scaffold.socket.util;

import cn.hutool.extra.spring.SpringUtil;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.stereotype.Component;


/**
 * socket发送消息用工具类
 *
 * @author machenike
 */
@Component
public class NettySocketUtil {
    private static final SocketIOServer server = SpringUtil.getBean(SocketIOServer.class);
    private static final String MESSAGE = "message";


    /**
     * 发送消息 指定客户端
     *
     * @param clientId
     * @param message
     */
    public static void sendMessage(String clientId, Object message) {

    }

    /**
     * 发送消息 全部客户端
     *
     * @param message
     */
    public static void sendNotice(Object message) {
        sendNotice(MESSAGE, message);
    }

    /**
     * 发送消息 全部客户端
     *
     * @param message
     */
    public static void sendNotice(String event, Object message) {
        server.getBroadcastOperations().sendEvent(event, message);
    }


}
