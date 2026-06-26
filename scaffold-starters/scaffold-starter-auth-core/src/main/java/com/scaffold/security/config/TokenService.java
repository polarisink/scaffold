package com.scaffold.security.config;

public interface TokenService {

    static String tokenPrefix(Long userId) {
        return "token:" + userId;
    }

    void set(Long userId, String token);

    String get(Long userId);

    boolean has(Long userId);

    void del(Long userId);
}
