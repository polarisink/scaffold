package com.scaffold.security.config;


/**
 * redis存储token的管理器
 */
public interface TokenService {


    /**
     * 设置用户id的token
     *
     * @param userId 用户id
     * @param token  token
     */
    void set(Long userId, String token);

    /**
     * 获取用户id的token
     *
     * @param userId 用户id
     * @return token
     */
    String get(Long userId);

    /**
     * 用户id token是否存在
     *
     * @param userId 用户id
     * @return token
     */
    boolean has(Long userId);

    /**
     * 删除用户token
     *
     * @param userId 用户id
     */
    void del(Long userId);
}
