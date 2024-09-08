package com.scaffold.core.jwt.vo;

import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @date: 2020-09-22 11:27
 * @author: wangjie
 */
@Getter
@Setter
public class PayloadDTO {


    @Value("${spring.application.name}")
    private String sub;

    /**
     * 签发时间
     */
    private Long iat = System.currentTimeMillis();

    /**
     * 过期时间
     */
    private Long exp = 365 * 24 * 60 * 60L;
    /**
     * JWT的ID
     */
    private String jti = sub + IdUtil.getSnowflake(1, 1);
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户name
     */
    private String username;

    /**
     * 用户拥有的角色
     */
    private List<String> authorities;

    public static PayloadDTO of(Long userId, String username, List<String> authorities) {
        PayloadDTO dto = new PayloadDTO();
        dto.userId = userId;
        dto.username = username;
        dto.authorities = authorities;
        return dto;
    }
}
