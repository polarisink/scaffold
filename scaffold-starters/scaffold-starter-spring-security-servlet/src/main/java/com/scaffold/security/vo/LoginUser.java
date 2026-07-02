package com.scaffold.security.vo;

import com.scaffold.base.util.CollUtils;
import lombok.Getter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.List;

@Getter
public class LoginUser extends User implements Serializable {

    private final Long userId;
    private final String username;
    private final List<String> auths;
    private String password;
    private Boolean status;

    public LoginUser(Long userId, String username, String password, List<String> auths) {
        super(username, password, CollUtils.toList(auths, org.springframework.security.core.authority.SimpleGrantedAuthority::new));
        this.userId = userId;
        this.password = password;
        this.status = true;
        this.username = username;
        this.auths = auths;
    }

    public LoginUser(Long userId, String username, List<String> auths) {
        super(username, "", CollUtils.toList(auths, org.springframework.security.core.authority.SimpleGrantedAuthority::new));
        this.userId = userId;
        this.username = username;
        this.auths = auths;
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication;
    }

    public static LoginUser currentUser() {
        Authentication a = getAuthentication();
        if (a == null) {
            return null;
        }
        if (a.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser;
        }
        Long userId = resolveUserId(a.getPrincipal());
        String username = username();
        if (userId == null || username == null) {
            return null;
        }
        return new LoginUser(userId, username, a.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    public static Long userId() {
        Authentication a = getAuthentication();
        if (a == null) {
            return null;
        }
        return resolveUserId(a.getPrincipal());
    }

    public static String username() {
        Authentication a = getAuthentication();
        if (a == null) {
            return null;
        }
        if (a.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser.getUsername();
        }
        Object credentials = a.getCredentials();
        return credentials == null ? null : credentials.toString();
    }

    private static Long resolveUserId(Object principal) {
        if (principal instanceof LoginUser loginUser) {
            return loginUser.getUserId();
        }
        if (principal instanceof Number number) {
            return number.longValue();
        }
        if (principal == null) {
            return null;
        }
        try {
            return Long.valueOf(principal.toString());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
