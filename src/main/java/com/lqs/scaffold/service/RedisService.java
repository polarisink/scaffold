package com.lqs.scaffold.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis Service
 *
 * @author lqs
 * @version 1.0
 * @since 2021-11-06
 */
@Slf4j
@Service
public class RedisService {

	public final StringRedisTemplate redisTemplate;

	public RedisService(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Map<String, String> hgetall(String key) {
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
		if (entries == null || entries.isEmpty()) {
			return new HashMap<>(0);
		}
		Map<String, String> result = new HashMap<>(entries.size());
		for (Object rawKey : entries.keySet()) {
			Object rawValue = entries.get(rawKey);
			result.put(rawKey.toString(), rawValue.toString());
		}
		return result;
	}

}
