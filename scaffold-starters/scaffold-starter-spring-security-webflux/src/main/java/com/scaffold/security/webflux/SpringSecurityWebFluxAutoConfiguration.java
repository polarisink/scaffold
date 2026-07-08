package com.scaffold.security.webflux;

import com.scaffold.security.config.TokenService;
import com.scaffold.security.util.JwtUtil;
import com.scaffold.security.vo.PayloadDTO;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class SpringSecurityWebFluxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil(SecurityProperties securityProperties) {
        return new JwtUtil(securityProperties.getToken().getJwtSecret());
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, WebFilter tokenWebFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .addFilterAt(tokenWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public WebFilter tokenWebFilter(SecurityProperties securityProperties, TokenService tokenService, JwtUtil jwtUtil) {
        PathMatcher pathMatcher = new AntPathMatcher();
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            for (String ignore : securityProperties.getIgnoreList()) {
                if (pathMatcher.match(ignore, path)) {
                    return chain.filter(exchange);
                }
            }
            String token = JwtUtil.getRealToken(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
            if (token == null || token.isEmpty()) {
                return unauthorized(exchange);
            }
            PayloadDTO dto = jwtUtil.resolveToken(token);
            if (tokenService.get(dto.getUserId().toString()) == null) {
                return unauthorized(exchange);
            }
            List<SimpleGrantedAuthority> authorities = dto.getAuthorities().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(dto.getUserId(), dto.getUsername(), authorities);
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        };
    }

    private static Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setRawStatusCode(401);
        return exchange.getResponse().setComplete();
    }
}
