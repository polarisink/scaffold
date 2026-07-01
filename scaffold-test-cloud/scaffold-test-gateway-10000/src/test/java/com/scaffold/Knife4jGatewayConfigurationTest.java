package com.scaffold;

import com.github.xiaoymin.knife4j.spring.gateway.Knife4jGatewayProperties;
import com.github.xiaoymin.knife4j.spring.gateway.discover.ServiceDiscoverHandler;
import com.github.xiaoymin.knife4j.spring.gateway.discover.ServiceRouterHolder;
import com.github.xiaoymin.knife4j.spring.gateway.discover.router.ConfigRouteServiceConvert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Knife4jGatewayConfigurationTest {

    @Test
    void shouldBindOpenApi3ServiceDiscoveryConfiguration() throws IOException {
        StandardEnvironment environment = new StandardEnvironment();
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load("application.yml", new ClassPathResource("application.yml"));
        sources.forEach(source -> environment.getPropertySources().addLast(source));

        Knife4jGatewayProperties properties = Binder.get(environment)
                .bind("knife4j.gateway", Knife4jGatewayProperties.class)
                .orElseThrow(AssertionError::new);

        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getDiscover().getEnabled()).isTrue();
        assertThat(properties.getDiscover().getVersion().name()).isEqualToIgnoringCase("openapi3");
        assertThat(properties.getDiscover().getExcludedServices())
                .containsExactlyInAnyOrder("cloud-gateway-.*", "dubbo-.*");
        assertThat(properties.getDiscover().getServiceConfig())
                .containsKeys("cloud-auth-10080", "cloud-provider-10081",
                        "cloud-consumer-10082", "cloud-order-10083");
        assertThat(properties.getDiscover().getServiceConfig().values())
                .allSatisfy(service -> assertThat(service.getContextPath()).isNull());

        GatewayProperties gateway = Binder.get(environment)
                .bind("spring.cloud.gateway.server.webflux", GatewayProperties.class)
                .orElseThrow(AssertionError::new);
        RouteDefinition authRoute = gateway.getRoutes().stream()
                .filter(route -> "cloud-auth-10080".equals(route.getId()))
                .findFirst()
                .orElseThrow(AssertionError::new);
        assertThat(authRoute.getFilters())
                .anySatisfy(filter -> {
                    assertThat(filter.getName()).isEqualTo("RewritePath");
                    assertThat(filter.getArgs()).containsEntry("_genkey_0", "/auth/(?<segment>v3/api-docs.*)");
                    assertThat(filter.getArgs()).containsEntry("_genkey_1", "/$\\{segment}");
                });

        ServiceDiscoverHandler discoverHandler = new ServiceDiscoverHandler(properties);
        ServiceRouterHolder holder = new ServiceRouterHolder(
                List.of("cloud-auth-10080", "cloud-provider-10081",
                        "cloud-consumer-10082", "cloud-order-10083"),
                Set.of(), discoverHandler);
        new ConfigRouteServiceConvert(gateway, properties).process(holder);

        assertThat(discoverHandler.getGatewayResources())
                .filteredOn(resource -> "cloud-consumer-10082".equals(resource.getServiceName()))
                .singleElement()
                .satisfies(resource -> {
                    assertThat(resource.getUrl()).isEqualTo("/consumer/v3/api-docs");
                    assertThat(resource.getContextPath()).isEqualTo("/consumer");
                });
    }
}
