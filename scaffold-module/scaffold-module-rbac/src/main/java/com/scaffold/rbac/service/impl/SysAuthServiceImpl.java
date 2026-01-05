package com.scaffold.rbac.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.scaffold.base.exception.BaseException;
import com.scaffold.log.LoginLogEvent;
import com.scaffold.rbac.service.SysAuthService;
import com.scaffold.rbac.vo.auth.LoginVO;
import com.scaffold.security.config.TokenService;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.LoginUser;
import com.scaffold.security.vo.PayloadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysAuthServiceImpl implements SysAuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
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
        //只记录成功日志，失败的不能进入系统，也没必要记
        SpringUtil.getApplicationContext().publishEvent(event);
        return token;
    }

    /**
     * 鉴权，并处理异常
     *
     * @param authenticationToken authenticationToken
     * @return authentication
     */
    private Authentication getAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {
        try {
            return authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            //不便于全局异常处理，而且这里的异常种类少，直接在这处理了
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
        //移除redis中token即可
        tokenService.del(LoginUser.userId());
    }
}
