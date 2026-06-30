package com.scaffold.security.config;


public interface TokenService{

    default String tokenPrefix(String userId) {
        return "token:" + userId;
    }

    void set(String userId, String token);

    String get(String userId);

    boolean has(String userId);

    void del(String userId);
}
