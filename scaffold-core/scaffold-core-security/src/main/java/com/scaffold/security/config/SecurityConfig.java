package com.scaffold.security.config;

import com.scaffold.core.base.constant.GlobalConstant;
import com.scaffold.core.base.util.R;
import com.scaffold.security.util.ResponseUtil;
import com.scaffold.security.vo.AuthCodeEnum;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL;

/**
 * security基础配置，在web环境生效，在webflux不会生效，彼此不会冲突
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    @Qualifier("tokenAndLogFilter")
    private final TokenAndLogFilter tokenAndLogFilter;


    /**
     * 安全链
     *
     * @param http http
     * @return chain
     * @throws Exception ex
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //设置INHERITABLE策略，可在子线程中获取用户信息
        SecurityContextHolder.setStrategyName(MODE_INHERITABLETHREADLOCAL);
        return http
                //tokenFilter放在UsernamePasswordAuthenticationFilter前面
                .addFilterBefore(tokenAndLogFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(c -> c
                        .requestMatchers(GlobalConstant.IGNORE_PATH_LIST).permitAll()
                        .anyRequest().authenticated())
                //异常处理
                .exceptionHandling(e -> e
                        //认证入口
                        .authenticationEntryPoint(authenticationEntryPoint())
                        //授权失败处理器
                        .accessDeniedHandler((request, response, ex) -> {
                            log.error("access denied path: {}", request.getRequestURI());
                            ResponseUtil.writeBody(response, AuthCodeEnum.ACCESS_DENIED);
                        })
                )
                .authenticationProvider(authenticationProvider())
                //禁用csrf
                .csrf(AbstractHttpConfigurer::disable)
                //跨域配置
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                //不需要默认的login
                .formLogin(AbstractHttpConfigurer::disable)
                //不需要默认的logout
                .logout(AbstractHttpConfigurer::disable)
                //禁用session，前后端分离不需要
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //禁用httpBasic
                .httpBasic(AbstractHttpConfigurer::disable)
                //配置认证管理器
                .build();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            String msg = AuthCodeEnum.UNAUTHORIZED.getMessage();
            if (ex.getMessage() != null) {
                msg = ex.getMessage();
            }
            Throwable cause = ex.getCause();
            if (cause != null && cause.getMessage() != null) {
                msg = cause.getMessage();
            }
            ResponseUtil.writeBody(response, R.failed(AuthCodeEnum.UNAUTHORIZED.getCode(), msg));
            log.error("{} unauthorized: {}", request.getRequestURI(), msg);
        };
    }

    /**
     * 密码编码器
     * 此种配置会给密码增加前缀 bcrypt
     *
     * @return 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 跨域配置
     */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("*"));
        cors.setAllowedMethods(List.of("*"));
        cors.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
