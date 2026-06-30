package com.scaffold.security.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenServiceImpl implements TokenService {

    public void set(String userId, String token) {
        log.info("{} set token", userId);
    }

    public String get(String userId) {
        log.info("{} set token", userId);
        return "";
    }

    public boolean has(String userId) {
        log.info("{} has token", userId);
        return true;
    }

    public void del(String userId) {
        log.info("{} del token", userId);
    }
}
