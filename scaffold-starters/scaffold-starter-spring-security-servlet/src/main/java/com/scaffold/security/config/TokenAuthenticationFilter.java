package com.scaffold.security.config;

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
            ResponseUtil.writeBody(response, AuthCodeEnum.UNAUTHORIZED);
            return;
        }
        PayloadDTO dto = JwtUtil.resolveToken(token);
        if (!tokenService.has(dto.getUserId())) {
            log.error("{} unauthorized: token is expired", url);
            ResponseUtil.writeBody(response, AuthCodeEnum.UNAUTHORIZED);
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
}
