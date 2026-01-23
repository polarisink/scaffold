package com.scaffold.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * redis存储token的管理器
 */
@Slf4j
@Component
//@ConditionalOnMissingBean(TokenService.class)
public class TokenServiceImpl implements TokenService {


    /**
     * 设置用户id的token
     *
     * @param userId 用户id
     * @param token  token
     */
    public void set(Long userId, String token) {
        log.info("{} set token", userId);
    }

    /**
     * 获取用户id的token
     *
     * @param userId 用户id
     * @return token
     */
    public String get(Long userId) {
        log.info("{} set token", userId);
        return "";
    }

    /**
     * 用户id token是否存在
     *
     * @param userId 用户id
     * @return token
     */
    public boolean has(Long userId) {
        log.info("{} has token", userId);
        return true;
    }

    /**
     * 删除用户token
     *
     * @param userId 用户id
     */
    public void del(Long userId) {
        log.info("{} del token", userId);
    }
}
