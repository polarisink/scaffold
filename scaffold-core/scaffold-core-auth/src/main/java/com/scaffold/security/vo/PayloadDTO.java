package com.scaffold.security.vo;

import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PayloadDTO {

    private String sub;
    private Long iat;
    private Long exp;
    private String jti;
    private Long userId;
    private String username;
    private List<String> authorities;

    public static PayloadDTO of(Long userId, String username, List<String> authorities) {
        PayloadDTO dto = new PayloadDTO();
        long now = Instant.now().getEpochSecond();
        dto.userId = userId;
        dto.sub = String.valueOf(userId);
        dto.username = username;
        dto.authorities = authorities;
        dto.iat = now;
        dto.exp = now + 365L * 24 * 60 * 60;
        dto.jti = buildJti(userId);
        return dto;
    }

    private static String buildJti(Long userId) {
        String prefix = userId == null ? "anonymous" : String.valueOf(userId);
        return prefix + "-" + IdUtil.getSnowflake(1, 1).nextId() + "-" + UUID.randomUUID();
    }
}
