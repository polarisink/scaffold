package github.polarisink.dao.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import github.polarisink.dao.extend.EasySqlInjector;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.logging.stdout.StdOutImpl;
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
    private final MetaObjectHandler metaObjectHandler;

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

    /**
     * yaml中sql-inceptor废弃，因此写在这里
     *
     * @return
     * @throws Exception
     */
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig().setIdType(IdType.AUTO)//自增id
                .setTableUnderline(false);//直接使用驼峰
        GlobalConfig globalConfig = new GlobalConfig().setSqlInjector(sqlInjector())//自定义组件
                .setMetaObjectHandler(metaObjectHandler)//自动注入的字段
                .setDbConfig(dbConfig);//数据库配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(false);//跟字段保持一致
        configuration.setLogImpl(StdOutImpl.class);//开启日志
        mybatisPlus.setGlobalConfig(globalConfig);//全局配置
        mybatisPlus.setDataSource(dataSource);//数据源
        //指定mapper和entity路径
        mybatisPlus.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:github/polarisink/dao/mapper/*.xml"));
        mybatisPlus.setTypeAliasesPackage("github.polarisink.dao.entity");
        mybatisPlus.setConfiguration(configuration);
        return mybatisPlus;
    }
}

