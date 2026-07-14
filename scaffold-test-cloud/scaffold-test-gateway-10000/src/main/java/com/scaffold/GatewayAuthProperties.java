package com.scaffold;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scaffold.gateway.auth")
public record GatewayAuthProperties(Boolean enabled, String authCheckUri, String[] ignorePathPatterns) {
    private static final String[] DEFAULT_IGNORE_PATH_PATTERNS = {
            "/auth/login",
            "/auth/v3/api-docs/**",
            "/actuator/**",
            "/favicon.ico",
            "/error",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/*/v3/api-docs/**"
    };
    public GatewayAuthProperties {
        enabled = enabled == null ? true : enabled;
        authCheckUri = authCheckUri == null ? "lb://cloud-auth-10080/auth/token-info" : authCheckUri;
        ignorePathPatterns = ignorePathPatterns == null ? DEFAULT_IGNORE_PATH_PATTERNS.clone() : ignorePathPatterns.clone();
    }
    public boolean isEnabled(){ return enabled; } public String getAuthCheckUri(){ return authCheckUri; }
    public String[] getIgnorePathPatterns(){ return ignorePathPatterns.clone(); }
}
