package com.scaffold.rbac.service;

import com.mzt.logapi.context.LogRecordContext;
import com.scaffold.base.exception.BaseException;
import com.scaffold.rbac.vo.auth.LoginVo;
import com.scaffold.security.config.TokenStore;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.LoginUser;
import com.scaffold.security.vo.PayloadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysAuthService implements ISysAuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenStore tokenStore;
    private final JwtUtil jwtUtil;

    @Override
    public String login(LoginVo vo) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    UsernamePasswordAuthenticationToken.unauthenticated(vo.username(), vo.password());
            Authentication a = getAuthentication(authenticationToken);
            LoginUser loginUser = (LoginUser) a.getPrincipal();
            List<String> roleCodeList = a.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            PayloadDTO dto = PayloadDTO.of(loginUser.getUserId(), loginUser.getUsername(), roleCodeList);
            Long userId = dto.getUserId();
            String token = jwtUtil.generateToken(dto);
            tokenStore.set(userId.toString(), token);
            LogRecordContext.putVariable("userId", userId);
            return token;
        } catch (RuntimeException exception) {
            throw exception;
        }
    }

    private Authentication getAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        try {
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            String msg = switch (e) {
                case InternalAuthenticationServiceException ee -> switch (ee.getCause()) {
                    case DisabledException ignored -> "用户被封禁，请联系管理员";
                    default -> "登录失败";
                };
                case BadCredentialsException ignored -> "用户名或密码错误";
                case UsernameNotFoundException ignored -> "用户不存在";
                default -> "登录失败";
            };
            throw new BaseException(msg);
        }
    }

    @Override
    public void logout() {
        Long userId = LoginUser.userId();
        String username = LoginUser.username();
        if (userId != null) {
            LogRecordContext.putVariable("userId", userId);
            LogRecordContext.putVariable("username", username);
            tokenStore.del(userId.toString());
        }
    }
}
