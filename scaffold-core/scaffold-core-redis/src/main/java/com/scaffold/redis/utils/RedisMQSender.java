package com.scaffold.redis.utils;

import com.scaffold.base.util.JsonUtil;
import com.scaffold.redis.domain.RedisMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author lqsgo
 */
@Slf4j
@Component
public class RedisMQSender {

    public static <T> void send(String queueName, T object) {
        if (object == null) {
            return;
        }
        RedisMessage<T> tRedisMessage = new RedisMessage<>(queueName, null, object, LocalDateTime.now());
        String json = JsonUtil.toJson(tRedisMessage);
        RedisUtils.streamAdd(queueName, json);
    }

}
