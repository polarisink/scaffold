package com.scaffold.orm.starter;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.scaffold.orm.DefaultMetaObjectHandler;
import com.scaffold.orm.MyBatisPlusConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(MyBatisPlusConfig.class)
public class OrmStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler();
    }
}
