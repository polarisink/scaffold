package com.scaffold;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.base.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GatewayAuthenticationFilter implements GlobalFilter, Ordered {
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final int AUTH_FAILED_CODE = 401;

    private final GatewayAuthProperties properties;
    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled() || shouldIgnore(exchange)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization)) {
            return unauthorized(exchange, "缺少登录凭证");
        }

        return loadBalancedWebClientBuilder.build()
                .get()
                .uri(properties.getAuthCheckUri())
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .bodyToMono(AuthCheckResult.class)
                .flatMap(result -> {
                    if (!isSuccess(result)) {
                        return unauthorized(exchange, result == null ? "登录凭证无效" : result.message());
                    }
                    return chain.filter(withUserHeaders(exchange, result));
                })
                .onErrorResume(error -> unauthorized(exchange, "登录凭证校验失败"));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    private boolean shouldIgnore(ServerWebExchange exchange) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return true;
        }
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        for (String pattern : properties.getIgnorePathPatterns()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSuccess(AuthCheckResult result) {
        return result != null && result.code() == R.SUCCESS_CODE && result.data() != null;
    }

    private ServerWebExchange withUserHeaders(ServerWebExchange exchange, AuthCheckResult result) {
        Object loginId = result.data().get("loginId");
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                .headers(headers -> headers.remove(USER_ID_HEADER));
        if (loginId != null) {
            requestBuilder.header(USER_ID_HEADER, loginId.toString());
        }
        return exchange.mutate().request(requestBuilder.build()).build();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = toJson(R.failed(AUTH_FAILED_CODE, StringUtils.hasText(message) ? message : "未登录"))
                .getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    private String toJson(R<Void> body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException exception) {
            return "{\"code\":401,\"message\":\"未登录\"}";
        }
    }

    public record AuthCheckResult(int code, String message, Map<String, Object> data) {
    }
}
