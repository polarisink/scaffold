package com.scaffold.rbac.service;

import com.mzt.logapi.beans.LogRecord;
import com.mzt.logapi.service.IOperatorGetService;
import com.scaffold.rbac.entity.SysLoginLog;
import com.scaffold.rbac.entity.SysOperateLog;
import com.scaffold.rbac.mapper.SysLoginLogMapper;
import com.scaffold.rbac.mapper.SysOperateLogMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RbacLogRecordServiceTest {

    private final SysOperateLogMapper operateMapper = mock(SysOperateLogMapper.class);
    private final SysLoginLogMapper loginMapper = mock(SysLoginLogMapper.class);
    private final ObjectProvider<IOperatorGetService> operatorProvider = mock(ObjectProvider.class);
    private final RbacLogRecordService service =
            new RbacLogRecordService(operateMapper, loginMapper, operatorProvider);

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
        service.recordLogin(null, "unknown", RbacLogRecordService.ACTION_LOGIN,
                false, "用户名或密码错误", "127.0.0.1", "test-agent");

        ArgumentCaptor<SysLoginLog> captor = ArgumentCaptor.forClass(SysLoginLog.class);
        verify(loginMapper).insert(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("unknown");
        assertThat(captor.getValue().getStatus()).isFalse();
        assertThat(captor.getValue().getMessage()).isEqualTo("用户名或密码错误");
        assertThat(captor.getValue().getIp()).isEqualTo("127.0.0.1");
    }
}
