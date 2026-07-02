package com.scaffold.rbac.service;

import com.scaffold.base.exception.BaseException;
import com.scaffold.rbac.entity.SysDictData;
import com.scaffold.rbac.entity.SysDictType;
import com.scaffold.rbac.mapper.SysDictDataMapper;
import com.scaffold.rbac.mapper.SysDictTypeMapper;
import com.scaffold.rbac.vo.dict.SysDictDataCreateVO;
import com.scaffold.rbac.vo.dict.SysDictTypeUpdateVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysDictServiceTest {

    private final SysDictTypeMapper typeMapper = mock(SysDictTypeMapper.class);
    private final SysDictDataMapper dataMapper = mock(SysDictDataMapper.class);

    @Test
    void updatesDataTypeWhenTypeCodeChanges() {
        SysDictType entity = type(1L, "旧类型", "old_type", true);
        when(typeMapper.selectById(1L)).thenReturn(entity);

        new SysDictTypeService(typeMapper, dataMapper).updateById(
                new SysDictTypeUpdateVO(1L, "新类型", "new_type", true, null));

        verify(dataMapper).updateDictType("old_type", "new_type");
        verify(typeMapper).updateById(entity);
    }

    @Test
    void rejectsDeletingTypeThatStillHasData() {
        when(typeMapper.selectById(1L)).thenReturn(type(1L, "状态", "sys_status", true));
        when(dataMapper.existsByDictType("sys_status")).thenReturn(true);

        assertThatThrownBy(() -> new SysDictTypeService(typeMapper, dataMapper).deleteById(1L))
                .isInstanceOf(BaseException.class)
                .hasMessage("请先删除该类型下的字典数据");

        verify(typeMapper, never()).deleteById(1L);
    }

    @Test
    void clearsPreviousDefaultWhenCreatingDefaultData() {
        when(typeMapper.findByDictType("sys_status"))
                .thenReturn(type(1L, "状态", "sys_status", true));
        doAnswer(invocation -> {
            SysDictData entity = invocation.getArgument(0);
            entity.setId(9L);
            return 1;
        }).when(dataMapper).insert(any(SysDictData.class));

        Long id = new SysDictDataService(dataMapper, typeMapper).save(new SysDictDataCreateVO(
                "sys_status", "启用", "1", 10, true, true, "success", null));

        assertThat(id).isEqualTo(9L);
        verify(dataMapper).clearDefault("sys_status", null);
    }

    @Test
    void disabledTypeReturnsNoBusinessOptions() {
        when(typeMapper.findByDictType("disabled_type"))
                .thenReturn(type(1L, "停用类型", "disabled_type", false));

        List<SysDictData> result = new SysDictDataService(dataMapper, typeMapper)
                .listByType("disabled_type");

        assertThat(result).isEmpty();
        verify(dataMapper, never()).listEnabledByType("disabled_type");
    }

    private static SysDictType type(Long id, String name, String code, boolean status) {
        SysDictType type = new SysDictType();
        type.setId(id);
        type.setDictName(name);
        type.setDictType(code);
        type.setStatus(status);
        return type;
    }
}
