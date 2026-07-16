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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 记录 Spring MVC 请求的访问日志。
 *
 * <p>过滤器通过 {@link ContentCachingRequestWrapper} 和
 * {@link ContentCachingResponseWrapper} 缓存请求、响应内容，但只有 JSON 内容会写入日志；
 * 非 JSON 响应仅记录请求信息、耗时和状态码。载荷大小受
 * {@link WebProperties.RequestLog#getMaxPayloadLength()} 限制。</p>
 *
 * <p>请求耗时大于 {@link WebProperties.RequestLog#getSlowThresholdMillis()} 时使用 WARN
 * 级别记录，否则使用 INFO 级别。配置的排除路径以及声明接受
 * {@code text/event-stream} 的 SSE 请求不会经过本过滤器。SSE 必须直接写入客户端，不能由
 * {@code ContentCachingResponseWrapper} 缓存，否则流会在过滤器返回时被提交并关闭。</p>
 *
 * @see WebProperties.RequestLog
 */
@Slf4j
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {
    private static final String TRUNCATED_SUFFIX = "... [truncated]";

    private final WebProperties webProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 判断当前请求是否跳过日志过滤。
     *
     * <p>SSE 请求需要直接向客户端持续写入数据，因此自动跳过；命中配置中
     * {@code exclude-path-patterns} 的请求同样跳过。</p>
     *
     * @param request 当前 HTTP 请求
     * @return {@code true} 表示不执行请求日志过滤，{@code false} 表示执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return acceptsEventStream(request) || webProperties.getRequestLog().getExcludePathPatterns().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }

    /**
     * 判断客户端是否声明接收 SSE 事件流。
     *
     * <p>{@link ContentCachingResponseWrapper} 会缓存响应，并在过滤器返回时复制响应内容。如果用于
     * SSE，首个事件后响应就会带着固定内容长度被提交，流随即关闭，导致 {@code EventSource}
     * 持续重连。因此，请求接受 {@code text/event-stream} 时必须跳过响应缓存。</p>
     *
     * @param request 当前 HTTP 请求
     * @return 任意 Accept 请求头与 {@code text/event-stream} 兼容时返回 {@code true}
     */
    private static boolean acceptsEventStream(HttpServletRequest request) {
        var acceptHeaders = request.getHeaders(HttpHeaders.ACCEPT);
        while (acceptHeaders.hasMoreElements()) {
            try {
                if (MediaType.parseMediaTypes(acceptHeaders.nextElement()).stream()
                        .anyMatch(MediaType.TEXT_EVENT_STREAM::isCompatibleWith)) {
                    return true;
                }
            } catch (IllegalArgumentException ignored) {
                // Let Spring MVC report malformed Accept headers as usual.
            }
        }
        return false;
    }

    /**
     * 执行请求日志过滤。
     *
     * <p>使用可缓存包装器收集请求体和响应体，在请求处理完成后输出访问日志，最后将缓存的
     * 响应内容复制到原始响应。即使日志格式化失败，也会继续复制响应，避免日志功能影响业务结果。</p>
     *
     * @param request 当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param filterChain Servlet 过滤器链
     * @throws ServletException 下游 Servlet 处理异常
     * @throws IOException 请求或响应读写异常
     */
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

    /**
     * 根据请求处理结果输出结构化访问日志。
     *
     * <p>普通请求使用 INFO 级别；耗时超过慢请求阈值时使用 WARN 级别。</p>
     *
     * @param request 已缓存请求内容的请求包装器
     * @param response 已缓存响应内容的响应包装器
     * @param consume 请求总耗时，单位毫秒
     * @param maxPayloadLength 请求体和响应体允许记录的最大字节数
     */
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

    /**
     * 将缓存的 JSON 载荷转换为可写入日志的内容。
     *
     * <p>空载荷和非 JSON 载荷返回空字符串；超过长度限制的载荷按字节截断并附加截断标记；
     * 未截断的 JSON 则解析为 JSON 树，避免以转义字符串形式输出。</p>
     *
     * @param content 缓存的原始载荷字节
     * @param contentType HTTP Content-Type
     * @param characterEncoding HTTP 字符编码
     * @param maxPayloadLength 最大记录字节数
     * @return 用于日志占位符的载荷对象
     */
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

    /**
     * 判断媒体类型是否与 JSON 兼容。
     *
     * @param contentType HTTP Content-Type 字符串
     * @return 是 JSON 兼容媒体类型时返回 {@code true}；为空或格式非法时返回 {@code false}
     */
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

    /**
     * 解析请求或响应声明的字符集。
     *
     * @param characterEncoding 字符集名称
     * @return 对应字符集；未声明或名称非法时回退为 UTF-8
     */
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
