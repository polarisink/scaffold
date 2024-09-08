package com.scaffold.redis.domain;

import java.time.LocalDateTime;

public record RedisMessage<T>(String streamKey, String msg, T data, LocalDateTime createdTime) {
}
