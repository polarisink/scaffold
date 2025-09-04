package com.scaffold.biz.module.rbac.components;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 密码工厂类
 */
@Component
public class PasswordFactory {
    private static final PasswordEncoder encoder = SpringUtil.getBean(PasswordEncoder.class);
    private static final RbacProperties rbacProperties = SpringUtil.getBean(RbacProperties.class);

    /**
     * 加密密码
     *
     * @param password 密码
     * @return 加密后的密码
     */
    public static String encode(String password) {
        return encoder.encode(password);
    }

    /**
     * 重置密码
     *
     * @param username 用户名
     * @return 重置后的密码
     */
    public static String reset(String username) {
        String passwd = switch (rbacProperties.getReset()) {
            case USERNAME -> username;
            case TIMESTAMP -> DateTimeFormatter.ofPattern(rbacProperties.getPattern()).format(LocalDateTime.now());
            case USERNAME_AND_TIMESTAMP -> {
                String timestamp = DateTimeFormatter.ofPattern(rbacProperties.getPattern()).format(LocalDateTime.now());
                String separator = rbacProperties.getSeparator();
                //通过配置生成密码
                yield rbacProperties.getUsernameBehind() ? timestamp + separator + username : username + separator + timestamp;
            }
        };
        return encode(passwd);
    }
}
