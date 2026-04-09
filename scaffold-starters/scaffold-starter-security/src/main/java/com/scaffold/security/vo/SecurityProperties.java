package com.scaffold.security.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private static final String[] DEFAULT_IGNORE_LIST = {"/auth/*", "/scenario/detail/*", "/favicon.ico", "/error", "/v3/api-docs/swagger-config", "/v3/api-docs/*", "/swagger-ui/**", "/*/v3/api-docs/**", "/swagger-ui.html", "/doc.html", "/webjars/**"};
    private String[] ignoreList;
    private Cors cors = new Cors();
    private Token token = new Token();

    public String[] getIgnoreList() {
        if (ignoreList == null || ignoreList.length == 0) {
            return DEFAULT_IGNORE_LIST;
        }
        String[] res = new String[DEFAULT_IGNORE_LIST.length + this.ignoreList.length];
        System.arraycopy(DEFAULT_IGNORE_LIST, 0, res, 0, DEFAULT_IGNORE_LIST.length);
        System.arraycopy(this.ignoreList, 0, res, DEFAULT_IGNORE_LIST.length, this.ignoreList.length);
        return res;
    }

    @Getter
    @Setter
    public static class Cors {
        private boolean enabled = false;
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedOriginPatterns = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        private List<String> allowedHeaders = new ArrayList<>(List.of("*"));
        private List<String> exposedHeaders = new ArrayList<>();
        private boolean allowCredentials = false;
        private Long maxAge = 3600L;
    }

    @Getter
    @Setter
    public static class Token {
        private String storeType = "memory";
        private long ttlMinutes = 30;
    }
}
