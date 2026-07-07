package com.scaffold.rbac.mapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import com.scaffold.rbac.entity.SysDictData;
import com.scaffold.rbac.vo.dict.SysDictDataPageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDictDataMapper extends MyBaseMapper<SysDictData> {

    default boolean existsByDictType(String dictType) {
        return exists(Wrappers.<SysDictData>lambdaQuery().eq(SysDictData::getDictType, dictType));
    }

    default boolean existsByLabel(String dictType, String dictLabel) {
        return exists(Wrappers.<SysDictData>lambdaQuery()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictLabel, dictLabel));
    }

    default boolean existsByValue(String dictType, String dictValue) {
        return exists(Wrappers.<SysDictData>lambdaQuery()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue));
    }

    default List<SysDictData> listEnabledByType(String dictType) {
        return selectList(Wrappers.<SysDictData>lambdaQuery()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, true)
                .orderByAsc(SysDictData::getDictSort, SysDictData::getId));
    }

    default void updateDictType(String oldType, String newType) {
        update(Wrappers.<SysDictData>lambdaUpdate()
                .eq(SysDictData::getDictType, oldType)
                .set(SysDictData::getDictType, newType));
    }

    default void clearDefault(String dictType, Long excludedId) {
        update(Wrappers.<SysDictData>lambdaUpdate()
                .eq(SysDictData::getDictType, dictType)
                .ne(excludedId != null, SysDictData::getId, excludedId)
                .set(SysDictData::getDefaultFlag, false));
    }

    default PageResponse<SysDictData> page(SysDictDataPageVO vo) {
        Page<SysDictData> page = selectPage(
                new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysDictData>lambdaQuery()
                        .eq(StrUtil.isNotBlank(vo.getDictType()), SysDictData::getDictType, vo.getDictType())
                        .like(StrUtil.isNotBlank(vo.getDictLabel()), SysDictData::getDictLabel, vo.getDictLabel())
                        .eq(vo.getStatus() != null, SysDictData::getStatus, vo.getStatus())
                        .orderByAsc(SysDictData::getDictSort, SysDictData::getId));
        return PageUtils.of(page);
    }
}
