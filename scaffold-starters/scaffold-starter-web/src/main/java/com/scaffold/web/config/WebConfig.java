package com.scaffold.web.config;

import com.scaffold.base.convert.StringCodeToEnumConverterFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * web配置，支持参数处理器
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WebProperties webProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebProperties.Cors cors = webProperties.getCors();
        if (!cors.isEnabled()) {
            log.info("web cors is disabled by default");
            return;
        }
        String[] allowedOrigins = cors.getAllowedOrigins().toArray(String[]::new);
        String[] allowedOriginPatterns = cors.getAllowedOriginPatterns().toArray(String[]::new);
        String[] allowedMethods = cors.getAllowedMethods().toArray(String[]::new);
        String[] allowedHeaders = cors.getAllowedHeaders().toArray(String[]::new);
        String[] exposedHeaders = cors.getExposedHeaders().toArray(String[]::new);
        var registration = registry.addMapping(cors.getPathPattern())
                .allowedMethods(allowedMethods.length == 0 ? List.of("GET", "POST", "PUT", "DELETE", "OPTIONS").toArray(String[]::new) : allowedMethods)
                .allowedHeaders(allowedHeaders.length == 0 ? new String[]{"*"} : allowedHeaders)
                .maxAge(cors.getMaxAge());
        if (allowedOrigins.length > 0) {
            registration.allowedOrigins(allowedOrigins);
        }
        if (allowedOriginPatterns.length > 0) {
            registration.allowedOriginPatterns(allowedOriginPatterns);
        }
        if (exposedHeaders.length > 0) {
            registration.exposedHeaders(exposedHeaders);
        }
        registration.allowCredentials(cors.isAllowCredentials());
    }

    /**
     * 配置枚举映射，枚举只需要实现IResponseEnum即可
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringCodeToEnumConverterFactory());
    }

}
