package com.scaffold.order;

import org.apache.seata.integration.http.JakartaTransactionPropagationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 将 Consumer 通过 TX_XID 请求头传来的全局事务绑定到 Order 线程。
 */
@Configuration
public class SeataWebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JakartaTransactionPropagationInterceptor())
                .addPathPatterns("/api/seata/**");
    }
}
