package com.scaffold.rbac.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.rbac.entity.SysOrg;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface SysOrgMapper extends MyBaseMapper<SysOrg> {

    default boolean existsByOrgName(String orgName) {
        return exists(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getOrgName, orgName));
    }

    default boolean existsByOrgCode(String orgCode) {
        return exists(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getOrgCode, orgCode));
    }

    default Optional<SysOrg> findByOrgCode(String orgCode) {
        return selectList(Wrappers.<SysOrg>lambdaQuery()
                        .eq(SysOrg::getOrgCode, orgCode))
                .stream()
                .findFirst();
    }

    default boolean existsByParentId(Long parentId) {
        return exists(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getParentId, parentId));
    }

    default long countByParentId(Long parentId) {
        return selectCount(Wrappers.<SysOrg>lambdaQuery().eq(SysOrg::getParentId, parentId));
    }
}
