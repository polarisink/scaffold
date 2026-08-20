package com.scaffold.orm.starter;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.scaffold.orm.MysqlInjector;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@EnableConfigurationProperties(ScaffoldOrmProperties.class)
public class MyBatisPlusAutoConfiguration {

    private final MetaObjectHandler metaObjectHandler;

    private final ScaffoldOrmProperties ormProperties;

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor paginationInterceptor() {
        JacksonTypeHandler.setObjectMapper(JacksonTypeHandler.getObjectMapper());
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        ScaffoldOrmProperties.Pagination properties = ormProperties.pagination();
        if (properties.enabled()) {
            PaginationInnerInterceptor pagination = properties.dbType() == null
                    ? new PaginationInnerInterceptor()
                    : new PaginationInnerInterceptor(properties.dbType());
            pagination.setOverflow(properties.overflow());
            pagination.setMaxLimit(properties.maxLimit());
            interceptor.addInnerInterceptor(pagination);
        }
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(metaObjectHandler);
        return globalConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public ISqlInjector sqlInjector() {
        return new MysqlInjector();
    }
}
