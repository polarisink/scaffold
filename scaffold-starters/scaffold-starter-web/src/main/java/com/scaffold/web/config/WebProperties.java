package com.scaffold.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "scaffold.web")
public class WebProperties {

    private Cors cors = new Cors();

    @Getter
    @Setter
    public static class Cors {
        private boolean enabled = false;
        private String pathPattern = "/**";
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedOriginPatterns = new ArrayList<>(List.of("http://localhost:*", "http://127.0.0.1:*"));
        private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        private List<String> allowedHeaders = new ArrayList<>(List.of("*"));
        private List<String> exposedHeaders = new ArrayList<>();
        private boolean allowCredentials = false;
        private long maxAge = 3600L;
    }
}
