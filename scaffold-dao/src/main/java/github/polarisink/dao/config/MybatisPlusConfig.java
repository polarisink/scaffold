package github.polarisink.dao.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import github.polarisink.dao.extend.EasySqlInjector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * mybatis配置项
 */
@Configuration
@RequiredArgsConstructor
public class MybatisPlusConfig {
    private final DataSource dataSource;
    /**
     * 新的分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public EasySqlInjector sqlInjector() {
        return new EasySqlInjector();
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory() throws Exception{
        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
        mybatisPlus.setDataSource(dataSource);
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSqlInjector(new EasySqlInjector());
        mybatisPlus.setGlobalConfig(globalConfig);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        mybatisPlus.setMapperLocations(resolver.getResources("classpath*:github/polarisink/dao/mapper/*.xml"));
        return mybatisPlus;
    }
}

