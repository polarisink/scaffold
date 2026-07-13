package com.scaffold.orm.starter;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class OrmStarterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OrmStarterAutoConfiguration.class));

    @Test
    void shouldRegisterOrmInfrastructure() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MetaObjectHandler.class);
            assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
        });
    }
}
