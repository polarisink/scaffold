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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = SwaggerProperties.PREFIX, name = "enabled", havingValue = "true")
public class OpenApiConfig {

    private final SwaggerProperties properties;

    @Bean
    public OpenAPI springShopOpenAPI() {
        SwaggerProperties.Contact contact = properties.getContact();
        OpenAPI openAPI = new OpenAPI()
                .info(new Info().title(properties.getTitle())
                        .description(properties.getDescription())
                        .version(properties.getVersion())
                        .contact(new Contact().name(contact.getName()).email(contact.getEmail()).url(contact.getUrl())))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                .components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
                        .name(HttpHeaders.AUTHORIZATION).type(SecurityScheme.Type.HTTP).scheme("bearer")));
        SwaggerProperties.ExternalDocs externalDocs = properties.getExternalDocs();
        if (externalDocs.getUrl() != null && !externalDocs.getUrl().isBlank()) {
            openAPI.externalDocs(new ExternalDocumentation()
                    .description(externalDocs.getDescription())
                    .url(externalDocs.getUrl()));
        }
        return openAPI;
    }
}
