package com.scaffold;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "scaffold.gateway.auth")
public class GatewayAuthProperties {
    private boolean enabled = true;
    private String authCheckUri = "lb://cloud-auth-10080/auth/token-info";
    private String[] ignorePathPatterns = {
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
}
