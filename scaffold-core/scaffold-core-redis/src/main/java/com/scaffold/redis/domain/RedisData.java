package com.scaffold.redis.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Redis 逻辑过期
 *
 * @author aries
 * @date 2022/09/01
 */
@Data
public class RedisData {

    /**
     * 到期时间
     */
    private LocalDateTime expireTime;
    /**
     * 数据
     */
    private Object data;
}
