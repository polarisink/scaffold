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

    private Response response = new Response();

    private RequestLog requestLog = new RequestLog();

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

    @Getter
    @Setter
    public static class Response {
        private String serverErrorMessage = "服务器或网络开小差了，请联系管理员";
        private List<String> ignoredClassNamePrefixes = new ArrayList<>(List.of(
                "org.springdoc.webmvc",
                "org.springframework.boot.actuate",
                "de.codecentric.boot.admin"
        ));
        private List<String> rawBodyPathPatterns = new ArrayList<>(List.of(
                "/actuator",
                "/actuator/**"
        ));
    }

    @Getter
    @Setter
    public static class RequestLog {
        private boolean enabled = true;
        private long slowThresholdMillis = 1000L;
        private int maxPayloadLength = 16 * 1024;
        private List<String> excludePathPatterns = new ArrayList<>();
    }
}
