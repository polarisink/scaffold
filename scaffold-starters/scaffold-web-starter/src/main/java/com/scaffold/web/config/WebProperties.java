package com.scaffold.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "scaffold.web")
public record WebProperties(Cors cors, Response response, RequestLog requestLog) {

    /**
     * MVC CORS 配置。默认关闭，避免 starter 在未显式配置时放开跨域访问。
     */
    public WebProperties {
        cors = cors == null ? new Cors() : cors;
        response = response == null ? new Response() : response;
        requestLog = requestLog == null ? new RequestLog() : requestLog;
    }

    public Cors getCors() { return cors; }
    public Response getResponse() { return response; }
    public RequestLog getRequestLog() { return requestLog; }

    /**
     * CORS 跨域配置。
     */
    @Getter
    @Setter
    public static class Cors {
        /**
         * 是否启用 MVC CORS 映射。
         */
        private boolean enabled = false;

        /**
         * CORS 映射路径，默认应用到所有路径。
         */
        private String pathPattern = "/**";

        /**
         * 精确允许的来源。为空时不设置 {@code allowedOrigins}。
         */
        private List<String> allowedOrigins = new ArrayList<>();

        /**
         * 按模式允许的来源，适合本地开发端口或通配子域名。
         */
        private List<String> allowedOriginPatterns = new ArrayList<>(List.of("http://localhost:*", "http://127.0.0.1:*"));

        /**
         * 允许的 HTTP 方法。为空时 WebConfig 会回退到常用方法集合。
         */
        private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        /**
         * 允许的请求头。默认允许全部请求头。
         */
        private List<String> allowedHeaders = new ArrayList<>(List.of("*"));

        /**
         * 允许浏览器暴露给前端脚本读取的响应头。
         */
        private List<String> exposedHeaders = new ArrayList<>();

        /**
         * 是否允许携带 Cookie、Authorization 等凭证。
         */
        private boolean allowCredentials = false;

        /**
         * 预检请求缓存时间，单位秒。
         */
        private long maxAge = 3600L;
    }

    /**
     * 统一响应配置。
     */
    @Getter
    @Setter
    public static class Response {
        /**
         * 未知异常对外返回的兜底错误文案。
         */
        private String serverErrorMessage = "服务器或网络开小差了，请联系管理员";

        /**
         * 不进行统一响应包装的返回值类名前缀，常用于 SpringDoc、Actuator 等框架端点。
         */
        private List<String> ignoredClassNamePrefixes = new ArrayList<>(List.of(
                "org.springdoc.webmvc",
                "org.springframework.boot.actuate",
                "de.codecentric.boot.admin"
        ));

        /**
         * 直接返回原始响应体的请求路径，命中后跳过 {@code R} 包装。
         */
        private List<String> rawBodyPathPatterns = new ArrayList<>(List.of(
                "/actuator",
                "/actuator/**"
        ));
    }

    /**
     * 请求日志配置。
     */
    @Getter
    @Setter
    public static class RequestLog {
        /**
         * 是否注册请求日志过滤器。
         */
        private boolean enabled = true;

        /**
         * 慢请求阈值，超过该耗时的请求使用 WARN 级别记录，单位毫秒。
         */
        private long slowThresholdMillis = 1000L;

        /**
         * 请求体和响应体日志最大记录长度，超过后截断。
         */
        private int maxPayloadLength = 16 * 1024;

        /**
         * 不记录请求日志的路径模式。
         */
        private List<String> excludePathPatterns = new ArrayList<>();
    }
}
