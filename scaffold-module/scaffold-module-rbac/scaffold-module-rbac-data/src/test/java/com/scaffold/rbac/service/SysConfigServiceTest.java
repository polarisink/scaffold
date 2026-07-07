package com.scaffold.rbac.service;

import com.scaffold.base.exception.BaseException;
import com.scaffold.rbac.entity.SysConfig;
import com.scaffold.rbac.mapper.SysConfigMapper;
import com.scaffold.rbac.vo.config.SysConfigCreateVO;
import com.scaffold.rbac.vo.config.SysConfigUpdateVO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SysConfigServiceTest {

    private final SysConfigMapper configMapper = mock(SysConfigMapper.class);
    private final SysConfigService service = new SysConfigService(configMapper);

    @Test
    void createsOrdinaryConfigByDefault() {
        doAnswer(invocation -> {
            SysConfig config = invocation.getArgument(0);
            config.setId(9L);
            return 1;
        }).when(configMapper).insert(any(SysConfig.class));

        Long id = service.save(new SysConfigCreateVO(
                "默认分页大小", "system.default-page-size", "20", null, "列表默认分页大小"));

        assertThat(id).isEqualTo(9L);
        verify(configMapper).insert(any(SysConfig.class));
    }

    @Test
    void rejectsChangingBuiltInConfigKey() {
        SysConfig config = config(1L, "system.original", true);
        when(configMapper.selectById(1L)).thenReturn(config);

        assertThatThrownBy(() -> service.updateById(new SysConfigUpdateVO(
                1L, "系统配置", "system.changed", "value", true, null)))
                .isInstanceOf(BaseException.class)
                .hasMessage("系统内置配置不能修改配置键");

        verify(configMapper, never()).updateById(any(SysConfig.class));
    }

    @Test
    void rejectsDeletingBuiltInConfig() {
        when(configMapper.selectById(1L)).thenReturn(config(1L, "system.original", true));

        assertThatThrownBy(() -> service.deleteById(1L))
                .isInstanceOf(BaseException.class)
                .hasMessage("系统内置配置不能删除");

        verify(configMapper, never()).deleteById(1L);
    }

    private static SysConfig config(Long id, String key, boolean system) {
        SysConfig config = new SysConfig();
        config.setId(id);
        config.setConfigName("系统配置");
        config.setConfigKey(key);
        config.setConfigValue("value");
        config.setSysFlag(system);
        return config;
    }
}
