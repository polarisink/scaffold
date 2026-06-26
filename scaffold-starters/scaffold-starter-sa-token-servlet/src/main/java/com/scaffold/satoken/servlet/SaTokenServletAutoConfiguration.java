package com.scaffold.satoken.servlet;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.scaffold.security.vo.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityProperties.class)
public class SaTokenServletAutoConfiguration implements WebMvcConfigurer {
    private final SecurityProperties securityProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> SaRouter.match("/**", StpUtil::checkLogin)))
                .addPathPatterns("/**")
                .excludePathPatterns(Arrays.asList(securityProperties.getIgnoreList()));
    }
}
