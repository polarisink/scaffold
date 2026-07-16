package com.scaffold.rbac.components;

import com.mzt.logapi.service.ILogRecordService;
import com.mzt.logapi.service.IOperatorGetService;
import com.mzt.logapi.service.impl.DefaultLogRecordServiceImpl;
import com.scaffold.rbac.mapper.SysLoginLogMapper;
import com.scaffold.rbac.mapper.SysOperateLogMapper;
import com.scaffold.rbac.service.RbacLogRecordService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@AutoConfiguration
@EnableConfigurationProperties(RbacProperties.class)
public class RbacPropertiesConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(value = ILogRecordService.class, ignored = DefaultLogRecordServiceImpl.class)
    public RbacLogRecordService rbacLogRecordService(
            SysOperateLogMapper operateLogMapper,
            SysLoginLogMapper loginLogMapper,
            ObjectProvider<IOperatorGetService> operatorServiceProvider,
            RbacProperties properties) {
        return new RbacLogRecordService(operateLogMapper, loginLogMapper, operatorServiceProvider, properties);
    }
}
