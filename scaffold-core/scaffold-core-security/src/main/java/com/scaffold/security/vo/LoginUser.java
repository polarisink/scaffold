package com.scaffold.security.vo;

import com.scaffold.core.base.util.CollUtils;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.List;

/**
 * 身份验证用户
 *
 * @author aries
 * @since 2024/07/25
 */
@Getter
public class LoginUser extends User implements Serializable {

    /**
     * 账号ID
     **/
    private final Long userId;
    /**
     * 姓名
     **/
    private final String username;

    /**
     * 角色
     */
    private final List<String> auths;
    /**
     * 密码
     */
    private String password;
    /**
     * 状态
     */
    private Boolean status;

    /**
     * 登录用户构造器
     *
     * @param userId   id
     * @param username 名字
     * @param password 密码
     * @param auths    身份验证
     */
    public LoginUser(Long userId, String username, String password, List<String> auths) {
        super(username, password, CollUtils.toList(auths, SimpleGrantedAuthority::new));
        this.userId = userId;
        this.password = password;
        this.status = true;
        this.username = username;
        this.auths = auths;
    }

    public LoginUser(Long userId, String username, List<String> auths) {
        super(username, "", CollUtils.toList(auths, SimpleGrantedAuthority::new));
        this.userId = userId;
        this.username = username;
        this.auths = auths;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static LoginUser currentUser() {
        Authentication a = getAuthentication();
        if (a == null) {
            return null;
        }
        return new LoginUser(Long.valueOf(a.getPrincipal().toString()), a.getCredentials().toString(), a.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    public static Long userId() {
        Authentication a = getAuthentication();
        if (a == null) {
            return null;
        }
        return Long.valueOf(a.getPrincipal().toString());
    }

    public static String username() {
        Authentication a = getAuthentication();
        if (a == null) {
            return null;
        }
        return a.getCredentials().toString();
    }
}
