package com.scaffold.security.config;

import com.scaffold.base.exception.BaseException;
import com.scaffold.base.util.R;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.util.ResponseUtil;
import com.scaffold.security.vo.AuthCodeEnum;
import com.scaffold.security.vo.PayloadDTO;
import com.scaffold.security.vo.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.scaffold.security.util.JwtUtil.getRealToken;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final PathMatcher pathMatcher;
    private final TokenService tokenService;
    private final SecurityProperties securityProperties;
    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String url = request.getRequestURI();
        for (String path : securityProperties.getIgnoreList()) {
            if (pathMatcher.match(path, url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String url = request.getRequestURI();
        String token = getRealToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (token == null || token.isEmpty()) {
            log.error("{} unauthorized: token is not present", url);
            writeUnauthorized(response, AuthCodeEnum.UNAUTHORIZED);
            return;
        }
        PayloadDTO dto;
        try {
            dto = jwtUtil.resolveToken(token);
        } catch (BaseException exception) {
            log.error("{} unauthorized: invalid token", url);
            writeUnauthorized(response, AuthCodeEnum.TOKEN_INVALID);
            return;
        }
        if (!tokenService.has(dto.getUserId().toString())) {
            log.error("{} unauthorized: token is expired", url);
            writeUnauthorized(response, AuthCodeEnum.TOKEN_EXPIRED);
            return;
        }
        List<SimpleGrantedAuthority> authorityList = dto.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        var authentication = new UsernamePasswordAuthenticationToken(
                dto.getUserId(), dto.getUsername(), authorityList);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private static void writeUnauthorized(HttpServletResponse response, AuthCodeEnum reason) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ResponseUtil.writeBody(response, R.failed(reason.getCode(), reason.getMessage()));
    }
}
