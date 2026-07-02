package com.scaffold.redis.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.scaffold.base.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.options.KeysScanOptions;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.client.codec.StringCodec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
public final class RedisUtils {

    private static final RedissonClient redisson = SpringUtil.getBean(RedissonClient.class);

    private static RBucket<String> stringBucket(String key) {
        return redisson.getBucket(key, StringCodec.INSTANCE);
    }

    private static RKeys keys() {
        return redisson.getKeys();
    }

    public static List<StreamGroup> groups(String key) {
        return redisson.<String, String>getStream(key, StringCodec.INSTANCE).listGroups();
    }

    public static void createGroup(String key, String group) {
        redisson.<String, String>getStream(key, StringCodec.INSTANCE).createGroup(StreamCreateGroupArgs.name(group).makeStream().id(StreamMessageId.ALL));
    }

    public static void acknowledge(String streamKey, String group, String recordId) {
        String[] parts = recordId.split("-");
        StreamMessageId messageId = new StreamMessageId(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        redisson.<String, String>getStream(streamKey, StringCodec.INSTANCE).ack(group, messageId);
    }

    public static void streamAdd(String streamKey, String object) {
        RStream<String, String> stream = redisson.getStream(streamKey, StringCodec.INSTANCE);
        stream.add(StreamAddArgs.entry("payload", object));
    }

    public static void convertAndSend(String topic, String message) {
        RTopic rTopic = redisson.getTopic(topic, StringCodec.INSTANCE);
        rTopic.publish(message);
    }

    public static boolean expire(String key, Duration duration) {
        try {
            return keys().expire(key, duration.toSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static Duration getExpire(String key) {
        long ttlMillis = keys().remainTimeToLive(key);
        return ttlMillis < 0 ? null : Duration.ofMillis(ttlMillis);
    }

    public static List<String> scan(String pattern) {
        return keysByPattern(pattern).collect(Collectors.toList());
    }

    public static List<String> findKeysForPage(String patternKey, int page, int size) {
        return keysByPattern(patternKey)
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    private static Stream<String> keysByPattern(String pattern) {
        Iterable<String> matchedKeys = keys().getKeys(KeysScanOptions.defaults().pattern(pattern));
        return StreamSupport.stream(matchedKeys.spliterator(), false);
    }

    public static boolean hasKey(String key) {
        try {
            return keys().countExists(key) > 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static void del(String... keys) {
        if (keys != null && keys.length > 0) {
            RedisUtils.keys().delete(keys);
        }
    }

    public static <R> R get(String key, Class<R> resultType) {
        String json = get(key);
        if (json != null && !json.isEmpty()) {
            return JsonUtil.redisRead(json, resultType);
        }
        return null;
    }

    public static String get(String key) {
        return key == null ? null : stringBucket(key).get();
    }

    public static List<String> multiGet(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        return keys.stream().map(RedisUtils::get).filter(Objects::nonNull).toList();
    }

    public static boolean set(String key, String value) {
        return set(key, value, null);
    }

    public static boolean set(String key, String value, Duration duration) {
        try {
            if (duration != null) {
                stringBucket(key).set(value, duration);
            } else {
                stringBucket(key).set(value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
