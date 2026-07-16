package com.scaffold.ai.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

final class AiApiKeyInterceptor implements HandlerInterceptor {

    private final ScaffoldAiProperties.Security security;

    AiApiKeyInterceptor(ScaffoldAiProperties.Security security) {
        this.security = security;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        byte[] expected = security.getApiKey().getBytes(StandardCharsets.UTF_8);
        byte[] actual = String.valueOf(request.getHeader(security.getHeader())).getBytes(StandardCharsets.UTF_8);
        if (expected.length > 0 && MessageDigest.isEqual(expected, actual)) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"AI API key is missing or invalid\"}");
        return false;
    }
}
