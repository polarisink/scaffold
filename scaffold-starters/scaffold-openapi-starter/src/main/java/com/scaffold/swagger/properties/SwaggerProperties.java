package com.scaffold.swagger.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 由 SwaggerStarterAutoConfiguration 统一注册，不能添加组件类注解，否则会产生重复 Bean。
 */
@ConfigurationProperties(prefix = SwaggerProperties.PREFIX)
public record SwaggerProperties(Boolean enabled, String basePackage, String groupName,
                                List<String> basePath, List<String> excludePath,
                                String title, String description, String version,
                                String license, String licenseUrl, String termsOfServiceUrl,
                                String host, Contact contact, ExternalDocs externalDocs,
                                Authorization authorization) {
    public static final String PREFIX = "scaffold.swagger";

    public SwaggerProperties {
        enabled = enabled != null && enabled;
        basePackage = basePackage == null ? "" : basePackage;
        groupName = groupName == null ? "default" : groupName;
        basePath = basePath == null ? List.of() : List.copyOf(basePath);
        excludePath = excludePath == null ? List.of() : List.copyOf(excludePath);
        title = title == null ? "接口文档" : title;
        description = description == null ? "" : description;
        version = version == null ? "v1.0" : version;
        license = license == null ? "" : license;
        licenseUrl = licenseUrl == null ? "" : licenseUrl;
        termsOfServiceUrl = termsOfServiceUrl == null ? "" : termsOfServiceUrl;
        host = host == null ? "" : host;
        contact = contact == null ? new Contact() : contact;
        externalDocs = externalDocs == null ? new ExternalDocs() : externalDocs;
        authorization = authorization == null ? new Authorization() : authorization;
    }

    public Boolean getEnabled(){ return enabled; } public String getBasePackage(){ return basePackage; }
    public String getGroupName(){ return groupName; } public List<String> getBasePath(){ return basePath; }
    public List<String> getExcludePath(){ return excludePath; } public String getTitle(){ return title; }
    public String getDescription(){ return description; } public String getVersion(){ return version; }
    public String getLicense(){ return license; } public String getLicenseUrl(){ return licenseUrl; }
    public String getTermsOfServiceUrl(){ return termsOfServiceUrl; } public String getHost(){ return host; }
    public Contact getContact(){ return contact; } public ExternalDocs getExternalDocs(){ return externalDocs; }
    public Authorization getAuthorization(){ return authorization; }

    @Setter
    @Getter
    public static class Contact {
        private String name = "";
        private String url = "";
        private String email = "";
    }

    @Getter
    @Setter
    public static class ExternalDocs {
        private String description = "";
        private String url = "";
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
