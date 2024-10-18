package com.scaffold.security.config;

import com.scaffold.core.base.constant.GlobalConstant;
import com.scaffold.core.base.util.JsonUtil;
import com.scaffold.core.base.util.ServletUtils;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.PayloadDTO;
import com.scaffold.security.util.ResponseUtil;
import com.scaffold.security.vo.AuthCodeEnum;
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
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.scaffold.security.util.JwtUtil.getRealToken;

/**
 * token校验和日志打印filter， 每个请求都调用一次
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAndLogFilter extends OncePerRequestFilter {
    private final PathMatcher pathMatcher;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        for (String path : GlobalConstant.IGNORE_PATH_LIST) {
            if (pathMatcher.match(path, url)) {
                //白名单直接过，也不打印日志
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
        //todo 其实用户重新登录之后之前的token应该失效了，这里应该比较一下，但是为了调试方便暂时不改
        if (!tokenService.has(dto.getUserId())) {
            log.error("{} unauthorized: token is expired", url);
            ResponseUtil.writeBody(response, AuthCodeEnum.UNAUTHORIZED);
            return;
        }
        List<SimpleGrantedAuthority> authorityList = dto.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUserId(), dto.getUsername(), authorityList);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //这里只会打印业务日志,如果权限那一关都没过，那也不会到这里来
        doFilterWithLog(filterChain, request, response);
    }

    /**
     * 执行请求并打印日志
     *
     * @param filterChain chain
     * @param request     request
     * @param response    response
     * @throws ServletException e
     * @throws IOException      e
     */
    private void doFilterWithLog(FilterChain filterChain, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long start;
        long consume = 0;
        Object requestBody = "";
        Object responseBody = "";
        try {
            //缓存后才能重复消费，否则不能多次拿requestBody和responseBody
            //网上常用的通过WebUtils.getNativeRequest/Response不生效，应该直接new
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            start = System.currentTimeMillis();
            //执行业务
            filterChain.doFilter(requestWrapper, responseWrapper);
            //计算响应时间
            consume = System.currentTimeMillis() - start;
            if (Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
                requestBody = JsonUtil.readTree(requestWrapper.getContentAsByteArray());
            }
            if (Objects.equals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
                responseBody = JsonUtil.readTree(responseWrapper.getContentAsByteArray());
            }
            //缓存的数据给response，这一步很重要，否则客户端拿不到数据
            responseWrapper.copyBodyToResponse();
        } finally {
            //大于500ms使用警告
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
