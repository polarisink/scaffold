package com.scaffold.security.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "scaffold.security")
public record SecurityProperties(String[] ignoreList, Cors cors, Token token) {
    /**
     * 默认跳过认证过滤的路径。
     */
    private static final String[] DEFAULT_IGNORE_LIST = {
            "/files/**", "/file/**", "/auth/login", "/scenario/detail/*", "/actuator/**",
            "/favicon.ico", "/error", "/v3/api-docs/swagger-config", "/v3/api-docs/*",
            "/swagger-ui/**", "/*/v3/api-docs/**", "/swagger-ui.html", "/doc.html",
            "/webjars/**", "/v3/api-docs"
    };

    /**
     * 追加的认证忽略路径。配置后会与默认忽略路径合并，而不是覆盖默认值。
     */
    public SecurityProperties {
        cors = cors == null ? new Cors() : cors;
        token = token == null ? new Token() : token;
    }


    public String[] getIgnoreList() {
        if (ignoreList == null || ignoreList.length == 0) {
            return DEFAULT_IGNORE_LIST;
        }
        String[] res = new String[DEFAULT_IGNORE_LIST.length + this.ignoreList.length];
        System.arraycopy(DEFAULT_IGNORE_LIST, 0, res, 0, DEFAULT_IGNORE_LIST.length);
        System.arraycopy(this.ignoreList, 0, res, DEFAULT_IGNORE_LIST.length, this.ignoreList.length);
        return res;
    }

    public Cors getCors() { return cors; }
    public Token getToken() { return token; }

    @Getter
    @Setter
    public static class Cors {
        /**
         * 是否启用 Security CORS 配置。
         */
        private boolean enabled = false;

        /**
         * 精确允许的来源。为空时不设置 {@code allowedOrigins}。
         */
        private List<String> allowedOrigins = new ArrayList<>();

        /**
         * 按模式允许的来源，适合本地开发端口或通配子域名。
         */
        private List<String> allowedOriginPatterns = new ArrayList<>();

        /**
         * 允许的 HTTP 方法。
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
        private Long maxAge = 3600L;
    }

    @Getter
    @Setter
    public static class Token {
        /**
         * JWT 签名密钥。生产环境建议使用至少 32 字节的随机密钥，并通过环境变量注入。
         */
        private String jwtSecret = "adf1efcs123reqwefwewqdsafrtrgeew";

        /**
         * security_token 缓存过期时间。Caffeine 缓存下会覆盖全局缓存 TTL。
         */
        private Duration cacheTtl = Duration.ofMinutes(30);

        /**
         * security_token 缓存最大条目数。当前用于 Caffeine token 专用缓存。
         */
        private long cacheMaximumSize = 10000;
    }
}
