package com.scaffold.swagger.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = SwaggerProperties.PREFIX)
public class SwaggerProperties {
    public static final String PREFIX = "swagger";

    private Boolean enabled = false;
    private String basePackage = "";
    private String groupName = "default";
    private List<String> basePath = new ArrayList<>();
    private List<String> excludePath = new ArrayList<>();
    private String title = "接口文档";
    private String description = "";
    private String version = "v1.0";
    private String license = "";
    private String licenseUrl = "";
    private String termsOfServiceUrl = "";
    private String host = "";
    private Contact contact = new Contact();
    private Authorization authorization = new Authorization();

    @Setter
    @Getter
    public static class Contact {
        private String name = "";
        private String url = "";
        private String email = "";
    }

    @Getter
    @Setter
    public static class Authorization {
        private String name = "";
        private String authRegex = "^.*$";
        private List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        private List<String> tokenUrlList = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class AuthorizationScope {
        private String scope = "";
        private String description = "";
    }
}
