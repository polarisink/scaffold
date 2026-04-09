package com.scaffold.security.config;

import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.base.util.ServletUtils;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.util.ResponseUtil;
import com.scaffold.security.vo.AuthCodeEnum;
import com.scaffold.security.vo.PayloadDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.scaffold.security.util.JwtUtil.getRealToken;

@Slf4j
@RequiredArgsConstructor
public class TokenAndLogFilter extends OncePerRequestFilter {
    private final PathMatcher pathMatcher;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        for (String path : GlobalConstant.IGNORE_PATH_LIST) {
            if (pathMatcher.match(path, url)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
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
        List<SimpleGrantedAuthority> authorityList = dto.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUserId(), dto.getUsername(), authorityList);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        doFilterWithLog(filterChain, request, response);
    }

    private void doFilterWithLog(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long start;
        long consume = 0;
        Object requestBody = "";
        Object responseBody = "";
        try {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            start = System.currentTimeMillis();
            filterChain.doFilter(requestWrapper, responseWrapper);
            consume = System.currentTimeMillis() - start;
            if (Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
                requestBody = JsonUtil.readTree(requestWrapper.getContentAsByteArray());
            }
            if (Objects.equals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
                responseBody = JsonUtil.readTree(responseWrapper.getContentAsByteArray());
            }
            responseWrapper.copyBodyToResponse();
        } finally {
            log.atLevel(consume <= 1000 ? Level.INFO : Level.WARN).log("""
                    \n************************************************************************
                    * Request URI:      {} {}
                    * Ip:               {}
                    * Request Body:     {}
                    * Time Consume:     {} ms
                    * Response Body:    {}
                    ************************************************************************
                    """, request.getMethod(), request.getRequestURI(), ServletUtils.getClientIP(request), requestBody, consume, responseBody);
        }
    }
}
