package com.scaffold.rbac.components;

import com.mzt.logapi.service.ILogRecordService;
import com.scaffold.rbac.mapper.SysLoginLogMapper;
import com.scaffold.rbac.mapper.SysOperateLogMapper;
import com.scaffold.rbac.service.RbacLogRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RbacPropertiesConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RbacPropertiesConfiguration.class))
            .withUserConfiguration(MapperConfiguration.class);

    @Test
    void providesDatabaseLogRecorderByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ILogRecordService.class);
            assertThat(context.getBean(ILogRecordService.class)).isInstanceOf(RbacLogRecordService.class);
        });
    }

    @Test
    void backsOffWhenApplicationProvidesLogRecorder() {
        contextRunner.withUserConfiguration(CustomRecorderConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(ILogRecordService.class);
            assertThat(context).doesNotHaveBean(RbacLogRecordService.class);
            assertThat(context.getBean(ILogRecordService.class))
                    .isSameAs(context.getBean("customLogRecordService"));
        });
    }

    @Configuration(proxyBeanMethods = false)
    static class MapperConfiguration {

        @Bean
        SysOperateLogMapper sysOperateLogMapper() {
            return mock(SysOperateLogMapper.class);
        }

        @Bean
        SysLoginLogMapper sysLoginLogMapper() {
            return mock(SysLoginLogMapper.class);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomRecorderConfiguration {

        @Bean
        ILogRecordService customLogRecordService() {
            return mock(ILogRecordService.class);
        }
    }
}
