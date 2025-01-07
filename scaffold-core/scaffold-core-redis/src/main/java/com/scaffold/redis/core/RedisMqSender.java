package com.scaffold.redis.core;

import com.scaffold.core.base.util.JsonUtil;
import com.scaffold.redis.domain.RedisMessage;
import com.scaffold.redis.utils.RedisUtils;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * RedisMq消息发送器
 *
 * @author lqsgo
 */
@Component
public class RedisMqSender {

    /**
     * 发送数据
     *
     * @param streamKey key
     * @param object    数据
     * @param <T>       泛型
     */
    public static <T> void send(@Nonnull String streamKey, T object) {
        if (object == null) {
            return;
        }
        RedisMessage<T> redisMessage = new RedisMessage<>(streamKey, null, object, LocalDateTime.now());
        String json = JsonUtil.toJson(redisMessage);
        RedisUtils.convertAndSend(streamKey, json);
    }

}
