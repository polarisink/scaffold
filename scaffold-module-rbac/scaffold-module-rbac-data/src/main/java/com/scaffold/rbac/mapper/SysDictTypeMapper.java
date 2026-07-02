package com.scaffold.rbac.mapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import com.scaffold.rbac.entity.SysDictType;
import com.scaffold.rbac.vo.dict.SysDictTypePageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDictTypeMapper extends MyBaseMapper<SysDictType> {

    default boolean existsByDictName(String dictName) {
        return exists(Wrappers.<SysDictType>lambdaQuery().eq(SysDictType::getDictName, dictName));
    }

    default boolean existsByDictType(String dictType) {
        return exists(Wrappers.<SysDictType>lambdaQuery().eq(SysDictType::getDictType, dictType));
    }

    default SysDictType findByDictType(String dictType) {
        return selectOne(Wrappers.<SysDictType>lambdaQuery()
                .eq(SysDictType::getDictType, dictType)
                .last("limit 1"));
    }

    default List<SysDictType> listEnabled() {
        return selectList(Wrappers.<SysDictType>lambdaQuery()
                .eq(SysDictType::getStatus, true)
                .orderByAsc(SysDictType::getDictName));
    }

    default PageResponse<SysDictType> page(SysDictTypePageVO vo) {
        Page<SysDictType> page = selectPage(
                new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysDictType>lambdaQuery()
                        .like(StrUtil.isNotBlank(vo.getDictName()), SysDictType::getDictName, vo.getDictName())
                        .like(StrUtil.isNotBlank(vo.getDictType()), SysDictType::getDictType, vo.getDictType())
                        .eq(vo.getStatus() != null, SysDictType::getStatus, vo.getStatus())
                        .orderByDesc(SysDictType::getGmtModified));
        return PageUtils.of(page);
    }
}
