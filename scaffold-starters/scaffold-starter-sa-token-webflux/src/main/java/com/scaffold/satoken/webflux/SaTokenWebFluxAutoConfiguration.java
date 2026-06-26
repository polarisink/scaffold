package com.scaffold.satoken.webflux;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.scaffold.base.util.R;
import com.scaffold.security.vo.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
public class SaTokenWebFluxAutoConfiguration {

    @Bean
    public SaReactorFilter saReactorFilter(SecurityProperties securityProperties) {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude(securityProperties.getIgnoreList())
                .setAuth(obj -> SaRouter.match("/**", StpUtil::checkLogin))
                .setError(e -> R.failed(401, e.getMessage()));
    }
}
