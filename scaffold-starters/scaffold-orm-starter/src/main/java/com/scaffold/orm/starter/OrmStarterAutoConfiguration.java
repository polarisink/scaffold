package com.scaffold.orm.starter;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.scaffold.orm.DefaultMetaObjectHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;

@AutoConfiguration
@Import(MyBatisPlusAutoConfiguration.class)
@ImportRuntimeHints(MyBatisRuntimeHints.class)
public class OrmStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler();
    }
}
