package com.scaffold.web.config;

import com.scaffold.base.util.JsonUtil;
import com.scaffold.base.util.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {
    private static final String TRUNCATED_SUFFIX = "... [truncated]";

    private final WebProperties webProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return webProperties.getRequestLog().getExcludePathPatterns().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        int maxPayloadLength = Math.max(0, webProperties.getRequestLog().getMaxPayloadLength());
        var requestWrapper = new ContentCachingRequestWrapper(request, maxPayloadLength);
        var responseWrapper = new ContentCachingResponseWrapper(response);
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long consume = System.currentTimeMillis() - start;
            try {
                writeLog(requestWrapper, responseWrapper, consume, maxPayloadLength);
            } catch (RuntimeException ex) {
                log.warn("Failed to write request log for {} {}", request.getMethod(), request.getRequestURI(), ex);
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void writeLog(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                          long consume, int maxPayloadLength) {
        Object requestBody = payload(request.getContentAsByteArray(), request.getContentType(),
                request.getCharacterEncoding(), maxPayloadLength);
        Object responseBody = payload(response.getContentAsByteArray(), response.getContentType(),
                response.getCharacterEncoding(), maxPayloadLength);
        long slowThreshold = webProperties.getRequestLog().getSlowThresholdMillis();

        log.atLevel(consume <= slowThreshold ? Level.INFO : Level.WARN).log("""
                        \n************************************************************************
                        * Request URI:      {} {}
                        * Ip:               {}
                        * Request Body:     {}
                        * Time Consume:     {} ms
                        * Response Status:  {}
                        * Response Body:    {}
                        ************************************************************************
                        """, request.getMethod(), request.getRequestURI(), ServletUtils.getClientIP(request),
                requestBody, consume, response.getStatus(), responseBody);
    }

    private static Object payload(byte[] content, String contentType, String characterEncoding, int maxPayloadLength) {
        if (content.length == 0 || !isJson(contentType)) {
            return "";
        }
        int limit = Math.max(0, maxPayloadLength);
        boolean truncated = content.length > limit;
        byte[] visibleContent = truncated ? Arrays.copyOf(content, limit) : content;
        Charset charset = resolveCharset(characterEncoding);
        String text = new String(visibleContent, charset);
        if (truncated) {
            return text + TRUNCATED_SUFFIX;
        }
        return JsonUtil.readTree(visibleContent);
    }

    private static boolean isJson(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }
        try {
            return MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(contentType));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static Charset resolveCharset(String characterEncoding) {
        if (characterEncoding == null || characterEncoding.isBlank()) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(characterEncoding);
        } catch (IllegalArgumentException ignored) {
            return StandardCharsets.UTF_8;
        }
    }
}
