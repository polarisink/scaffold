package com.scaffold.security.config;

import com.scaffold.base.util.R;
import com.scaffold.security.util.ResponseUtil;
import com.scaffold.security.vo.AuthCodeEnum;
import com.scaffold.security.vo.SecurityProperties;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    @Qualifier("tokenAuthenticationFilter")
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityProperties securityProperties, PasswordEncoder passwordEncoder) throws Exception {
        SecurityContextHolder.setStrategyName(MODE_INHERITABLETHREADLOCAL);
        return http
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(c -> c
                        .requestMatchers(securityProperties.getIgnoreList()).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler((request, response, ex) -> {
                            log.error("access denied path: {}", request.getRequestURI());
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            ResponseUtil.writeBody(response, R.failed(
                                    AuthCodeEnum.ACCESS_DENIED.getCode(),
                                    AuthCodeEnum.ACCESS_DENIED.getMessage()));
                        })
                )
                .authenticationProvider(authenticationProvider(passwordEncoder))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            String message = AuthCodeEnum.UNAUTHORIZED.getMessage();
            if (ex.getMessage() != null) {
                message = ex.getMessage();
            }
            Throwable cause = ex.getCause();
            if (cause != null && cause.getMessage() != null) {
                message = cause.getMessage();
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ResponseUtil.writeBody(response, R.failed(AuthCodeEnum.UNAUTHORIZED.getCode(), message));
            log.error("{} unauthorized: {}", request.getRequestURI(), message);
        };
    }

    public CorsConfigurationSource corsConfigurationSource() {
        SecurityProperties.Cors corsProperties = securityProperties.getCors();
        CorsConfiguration cors = new CorsConfiguration();
        if (!corsProperties.isEnabled()) {
            cors.setAllowedOrigins(List.of());
            cors.setAllowedOriginPatterns(List.of());
            cors.setAllowedMethods(List.of());
            cors.setAllowedHeaders(List.of());
        } else {
            cors.setAllowedOrigins(new ArrayList<>(corsProperties.getAllowedOrigins()));
            cors.setAllowedOriginPatterns(new ArrayList<>(corsProperties.getAllowedOriginPatterns()));
            cors.setAllowedMethods(new ArrayList<>(corsProperties.getAllowedMethods()));
            cors.setAllowedHeaders(new ArrayList<>(corsProperties.getAllowedHeaders()));
            cors.setExposedHeaders(new ArrayList<>(corsProperties.getExposedHeaders()));
            cors.setAllowCredentials(corsProperties.isAllowCredentials());
            cors.setMaxAge(corsProperties.getMaxAge());
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
