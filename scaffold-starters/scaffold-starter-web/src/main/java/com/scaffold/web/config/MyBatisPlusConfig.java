package com.scaffold.web.config;


import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.scaffold.orm.MysqlInjector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus配置
 *
 * @author aries
 * @date 2022/09/13
 */
@Configuration
@RequiredArgsConstructor
public class MyBatisPlusConfig {

    private final MetaObjectHandler metaObjectHandler;

    /**
     * mybatis-plus分页插件  3.4版本后使用
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        // JacksonTypeHandler设置自定义的objectMapper
        JacksonTypeHandler.setObjectMapper(JacksonTypeHandler.getObjectMapper());
        return new MybatisPlusInterceptor();
    }


    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
    }

    /**
     * 自动填充功能
     * 主要用到数据添加时间，修改时间自动填充
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(metaObjectHandler);
        return globalConfig;
    }

    @Bean
    public ISqlInjector sqlInjector() {
        return new MysqlInjector();
    }
}
