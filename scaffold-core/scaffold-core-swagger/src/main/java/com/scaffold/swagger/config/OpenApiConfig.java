package com.scaffold.swagger.config;

import com.scaffold.swagger.properties.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;


@RequiredArgsConstructor
@Configuration
public class OpenApiConfig {

    private final SwaggerProperties properties;

    @Bean
    public OpenAPI springShopOpenAPI() {
        SwaggerProperties.Contact contact = properties.getContact();
        return new OpenAPI()
                // 接口文档标题
                .info(new Info().title(properties.getTitle())
                        // 接口文档简介
                        .description(properties.getDescription())
                        // 接口文档版本
                        .version(properties.getVersion())
                        // 开发者联系方式
                        .contact(new Contact().name(contact.getName()).email(contact.getEmail()).url(contact.getUrl())))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringBoot基础框架")
                        .url("http://127.0.0.1:8088"))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION)).components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
                        .name(HttpHeaders.AUTHORIZATION).type(SecurityScheme.Type.HTTP).scheme("bearer")));
    }
}
