package com.scaffold.rocket;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 存储火箭高度的缓存
 * todo 位置是否做持久化
 */
@Service
@RequiredArgsConstructor
public class RocketCache {

    private final RocketRepository rocketRepository;

    private static final String ROCKET_POSITION = "rocketPosition";

    /**
     * 获取当前高度
     *
     * @param rocketId 火箭id
     * @return 高度
     */
    @Cacheable(value = ROCKET_POSITION, key = "#rocketId")
    public double getCurrentHeight(String rocketId) {
        Rocket rocket = rocketRepository.findById(rocketId).orElseThrow(() -> new RuntimeException("Rocket not found"));
        return rocket.getInitialHeight();
    }

    /**
     * 更新当前高度
     *
     * @param rocketId      火箭id
     * @param currentHeight 当前高度
     * @return 新高度
     */
    @CachePut(value = ROCKET_POSITION, key = "#rocketId")
    public double updateHeight(String rocketId, Double currentHeight) {
        Rocket rocket = rocketRepository.findById(rocketId).orElseThrow(() -> new RuntimeException("Rocket not found"));
        return currentHeight + rocket.getSpeed();
    }
}