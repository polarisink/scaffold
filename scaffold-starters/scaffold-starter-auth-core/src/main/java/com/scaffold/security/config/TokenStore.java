package com.scaffold.security.config;

/**
 * token服务
 */
public interface TokenStore {

    String TOKEN_CACHE_NAME = "security_token";

    /**
     * 设置token
     *
     * @param userId 优化id
     * @param token  token
     */
    void set(String userId, String token);

    /**
     * 获取token
     *
     * @param userId 优化id
     * @return token
     */
    String get(String userId);

    /**
     * 是否包含token
     *
     * @param userId 优化id
     * @return 是否包含
     */
    boolean has(String userId);

    /**
     * 删除token
     *
     * @param userId 优化id
     */
    void del(String userId);
}
