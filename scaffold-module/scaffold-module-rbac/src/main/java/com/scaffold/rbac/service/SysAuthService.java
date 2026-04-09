package com.scaffold.rbac.service;

import cn.hutool.extra.spring.SpringUtil;
import com.scaffold.base.exception.BaseException;
import com.scaffold.log.LoginLogEvent;
import com.scaffold.rbac.vo.auth.LoginVO;
import com.scaffold.security.config.TokenService;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.LoginUser;
import com.scaffold.security.vo.PayloadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysAuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public String login(LoginVO vo) {
        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(vo.username(), vo.password());
        Authentication a = getAuthentication(authenticationToken);
        LoginUser loginUser = (LoginUser) a.getPrincipal();
        List<String> roleCodeList = a.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        PayloadDTO dto = PayloadDTO.of(loginUser.getUserId(), loginUser.getUsername(), roleCodeList);
        Long userId = dto.getUserId();
        String username = dto.getUsername();
        String token = JwtUtil.generateToken(dto);
        tokenService.set(userId, token);
        LoginLogEvent event = new LoginLogEvent();
        event.setUsername(vo.username());
        event.setUserId(userId);
        event.setUsername(username);
        SpringUtil.getApplicationContext().publishEvent(event);
        return token;
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

    public void logout() {
        tokenService.del(LoginUser.userId());
    }
}
