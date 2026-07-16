package com.scaffold.rbac.service;

import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.rbac.components.RbacProperties;
import com.scaffold.rbac.contant.RbacLogConst;
import com.scaffold.rbac.entity.SysLoginLog;
import com.scaffold.rbac.entity.SysOperateLog;
import com.scaffold.rbac.mapper.SysLoginLogMapper;
import com.scaffold.rbac.mapper.SysOperateLogMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RbacLogRecordServiceTest {

    private final SysOperateLogMapper operateMapper = mock(SysOperateLogMapper.class);
    private final SysLoginLogMapper loginMapper = mock(SysLoginLogMapper.class);
    private final ObjectProvider<IOperatorGetService> operatorProvider = mock(ObjectProvider.class);
    private final RbacProperties properties = mock(RbacProperties.class);
    private final RbacLogRecordService service =
            new RbacLogRecordService(operateMapper, loginMapper, operatorProvider, properties);

    RbacLogRecordServiceTest() {
        when(properties.logEnabled()).thenReturn(true);
    }

    @Test
    void persistsBizlogRecordAsOperationLog() {
        service.record(LogRecord.builder()
                .type("用户模块")
                .subType("修改用户")
                .bizNo("42")
                .operator("admin")
                .action("修改用户 admin")
                .fail(false)
                .build());

        ArgumentCaptor<SysOperateLog> captor = ArgumentCaptor.forClass(SysOperateLog.class);
        verify(operateMapper).insert(captor.capture());
        assertThat(captor.getValue().getOperator()).isEqualTo("admin");
        assertThat(captor.getValue().getStatus()).isTrue();
        assertThat(captor.getValue().getBizNo()).isEqualTo("42");
    }

    @Test
    void persistsFailedLoginWithReason() {
        service.record(LogRecord.builder()
                .type(RbacLogConst.AUTH)
                .subType(RbacLogConst.LOGIN)
                .operator("unknown")
                .action("用户名或密码错误")
                .fail(true)
                .build());

        ArgumentCaptor<SysLoginLog> captor = ArgumentCaptor.forClass(SysLoginLog.class);
        verify(loginMapper).insert(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("unknown");
        assertThat(captor.getValue().getStatus()).isFalse();
        assertThat(captor.getValue().getMessage()).isEqualTo("用户名或密码错误");
    }

    @Test
    void doesNotPersistLogWhenLoggingIsDisabled() {
        when(properties.logEnabled()).thenReturn(false);

        service.record(LogRecord.builder()
                .type("用户模块")
                .action("修改用户")
                .build());

        verify(operateMapper, never()).insert(org.mockito.ArgumentMatchers.any(SysOperateLog.class));
        verify(loginMapper, never()).insert(org.mockito.ArgumentMatchers.any(SysLoginLog.class));
    }
}
