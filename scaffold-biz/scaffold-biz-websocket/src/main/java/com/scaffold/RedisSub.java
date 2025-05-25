package com.scaffold;

import com.scaffold.socket.util.NettySocketUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisSub {
    private final RedisMessageListenerContainer container;

    @PostConstruct
    public void add() {
        //直接订阅所有，直接转发
        container.addMessageListener(rocketListener(), new PatternTopic("*"));
    }

    public MessageListener rocketListener() {
        return (message, pattern) -> {
            //rocket device
            String channel = new String(message.getChannel());
            if (channel.startsWith("rocket")) {

            }
            NettySocketUtil.sendNotice(channel, new String(message.getBody()));
        };
    }

    void xxx() {
    }

    public MessageListener deviceListener() {
        return (message, pattern) -> {
            String channel = new String(message.getChannel());
            //以redis的channel作为ws的event直接发送
            NettySocketUtil.sendNotice(channel, new String(message.getBody()));
            log.info("channel: {},message: {},body: {}", channel, message, new String(message.getBody()));
        };
    }

    public MessageListener incidentListener() {
        return (message, pattern) -> {
            String channel = new String(message.getChannel());
            //以redis的channel作为ws的event直接发送
            NettySocketUtil.sendNotice(channel, new String(message.getBody()));
            log.info("channel: {},message: {},body: {}", channel, message, new String(message.getBody()));
        };
    }
}
