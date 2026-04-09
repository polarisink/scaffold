package com.scaffold.security.vo;

import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PayloadDTO {

    private String sub;
    private Long iat = System.currentTimeMillis();
    private Long exp = 365 * 24 * 60 * 60L;
    private String jti = sub + IdUtil.getSnowflake(1, 1);
    private Long userId;
    private String username;
    private List<String> authorities;

    public static PayloadDTO of(Long userId, String username, List<String> authorities) {
        PayloadDTO dto = new PayloadDTO();
        dto.userId = userId;
        dto.username = username;
        dto.authorities = authorities;
        return dto;
    }
}
