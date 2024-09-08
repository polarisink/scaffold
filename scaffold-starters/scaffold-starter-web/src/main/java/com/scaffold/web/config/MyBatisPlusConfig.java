//package com.scaffold.web.config;
//
//
//import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
//import com.baomidou.mybatisplus.core.config.GlobalConfig;
//import com.baomidou.mybatisplus.core.injector.ISqlInjector;
//import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
//import com.scaffold.orm.extension.MysqlInjector;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * mybatis-plus配置
// *
// * @author aries
// * @date 2022/09/13
// */
//@Configuration
//public class MyBatisPlusConfig {
//
//    /**
//     * mybatis-plus分页插件  3.4版本后使用
//     */
//    @Bean
//    public MybatisPlusInterceptor paginationInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
//        return interceptor;
//    }
//
//
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return configuration -> configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
//    }
//
//    /**
//     * 自动填充功能
//     * 主要用到数据添加时间，修改时间自动填充
//     */
//    @Bean
//    public GlobalConfig globalConfig() {
//        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig.setMetaObjectHandler(new MyBatisMetaHandler());
//        return globalConfig;
//    }
//
//    @Bean
//    public ISqlInjector sqlInjector() {
//        return new MysqlInjector();
//    }
//}
