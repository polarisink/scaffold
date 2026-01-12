package com.scaffold;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.redis.annotations.RedisStreamListener;
import com.scaffold.redis.annotations.RedisSubTopic;
import com.scaffold.redis.core.RedisMqSender;
import com.scaffold.redis.domain.RedisMessage;
import com.scaffold.socket.util.NettySocketUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * socket处理拦截器
 *
 * @author machenike
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettySocketHandler implements CommandLineRunner, DisposableBean {
    private final static String QUERY_CLIENT_ID = "clientId";
    /**
     * 客户端保存用Map
     */
    public static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();
    /**
     * 连接数
     */
    public static AtomicInteger onlineCount = new AtomicInteger(0);
    private final SocketIOServer server;

    /**
     * 客户端连上socket服务器时执行此事件
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        RedisMqSender.send("web", "hello world");
    }


    /**
     * 客户端断开socket服务器时执行此事件
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String clientId = client.getHandshakeData().getSingleUrlParam(QUERY_CLIENT_ID);
        if (clientId != null) {
            clientMap.remove(clientId);
            client.disconnect();
            onlineCount.addAndGet(-1);
            log.debug("disconnect success: [clientId={},onlineCount={}]", clientId, onlineCount.get());
        }
    }

    /**
     * @param client
     */
    @OnEvent(value = "message")
    public void onMessage(SocketIOClient client, AckRequest request, Object data) {
        RedisMqSender.send("web", "hello world");
    }

    @RedisStreamListener(queueName = "web")
    public void read(String s) {
        NettySocketUtil.sendNotice("hh", "redis read");
        System.out.println("redis read");
    }

    @Bean
    @RedisSubTopic(topic = "redis.chat.*")
    public MessageListener chatMessageListener() {
        return (Message message, byte[] pattern) -> {
            RedisMessage<String> m = JsonUtil.read(message.getBody(), new TypeReference<>() {
            });
            System.out.println("Chat message received: " + new String(message.getBody()));
        };
    }

    @Bean
    @RedisSubTopic(topic = "redis.news.*")
    public MessageListener newsMessageListener() {
        return (Message message, byte[] pattern) -> {
            String topic = new String(pattern);
            System.out.println("News message received: " + new String(message.getBody()));
        };
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
    }
}
