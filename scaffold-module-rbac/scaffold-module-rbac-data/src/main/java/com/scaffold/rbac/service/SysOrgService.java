package com.scaffold.rbac.service;

import com.mzt.logapi.starter.annotation.LogRecord;
import com.scaffold.rbac.components.RbacCache;
import com.scaffold.rbac.contant.RbacResultEnum;
import com.scaffold.rbac.entity.SysOrg;
import com.scaffold.rbac.mapper.SysOrgMapper;
import com.scaffold.rbac.mapper.SysUserMapper;
import com.scaffold.rbac.vo.org.SysOrgCreateVO;
import com.scaffold.rbac.vo.org.SysOrgUpdateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SysOrgService {

    private final SysOrgMapper sysOrgMapper;
    private final SysUserMapper sysUserMapper;
    private final RbacCache rbacCache;


    @Transactional(readOnly = true)
    public List<SysOrg> tree() {
        return rbacCache.orgTree();
    }


    // @formatter:off
    @LogRecord(type = "组织模块",// 大类
            subType = "新增组织",// 小类
            success = "新增组织【{{#vo.orgName}}】，菜单ID：{{#_ret}}",// 成功日志
            fail = "新增菜单【{{#vo.orgName}}】失败，原因：{{#_errorMsg}}",// 失败日志
            bizNo = "{{#_ret}}",  // 使用返回值（新菜单ID）作为业务编号
            extra = "{{#vo.toString()}}"  // 记录完整的创建请求
    )
    // @formatter:on
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysOrgCreateVO vo) {
        RbacResultEnum.UNIQUE_ORG_NAME.isFalse(sysOrgMapper.existsByOrgName(vo.orgName()));
        RbacResultEnum.UNIQUE_ORG_CODE.isFalse(sysOrgMapper.existsByOrgCode(vo.orgCode()));
        validateParent(vo.parentId(), null);

        SysOrg entity = new SysOrg();
        BeanUtils.copyProperties(vo, entity);
        if (entity.getSort() == null) {
            entity.setSort((int) sysOrgMapper.countByParentId(vo.parentId()) * 10);
        }
        sysOrgMapper.insert(entity);
        rbacCache.orgClear();
        return entity.getId();
    }

    // @formatter:off
    @LogRecord(
            success = "更新组织【{{#vo.orgName}}】，菜单ID：{{#vo.id}}",
            type = "组织模块",
            subType = "更新组织",
            bizNo = "{{#vo.id}}",
            fail = "更新组织【{{#vo.orgName}}】失败，原因：{{#_errorMsg}}"
    )
    // @formatter:on
    @Transactional(rollbackFor = Exception.class)
    public void updateById(SysOrgUpdateVO vo) {
        SysOrg entity = sysOrgMapper.selectById(vo.id());
        RbacResultEnum.ORG_NOT_FOUND.notNull(entity);
        RbacResultEnum.UNIQUE_ORG_NAME.isTrue(Objects.equals(entity.getOrgName(), vo.orgName())
                || !sysOrgMapper.existsByOrgName(vo.orgName()));
        RbacResultEnum.UNIQUE_ORG_CODE.isTrue(Objects.equals(entity.getOrgCode(), vo.orgCode()) || !sysOrgMapper.existsByOrgCode(vo.orgCode()));
        validateParent(vo.parentId(), vo.id());
        BeanUtils.copyProperties(vo, entity);
        sysOrgMapper.updateById(entity);
        rbacCache.orgClear();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long orgId) {
        RbacResultEnum.ORG_NOT_FOUND.notNull(sysOrgMapper.selectById(orgId));
        RbacResultEnum.CAN_NOT_DELETE_PARENT_ORG_NODE.isFalse(sysOrgMapper.existsByParentId(orgId));
        RbacResultEnum.CAN_NOT_DELETE_ORG_HAS_USER.isFalse(sysUserMapper.existsByOrgId(orgId));
        sysOrgMapper.deleteById(orgId);
        rbacCache.orgClear();
    }

    private void validateParent(Long parentId, Long currentOrgId) {
        if (Objects.equals(parentId, 0L)) {
            return;
        }
        Set<Long> visited = new HashSet<>();
        Long cursor = parentId;
        while (!Objects.equals(cursor, 0L)) {
            RbacResultEnum.ORG_TREE_CYCLE.isFalse(
                    Objects.equals(cursor, currentOrgId) || !visited.add(cursor));
            SysOrg parent = sysOrgMapper.selectById(cursor);
            RbacResultEnum.ORG_PARENT_NOT_FOUND.notNull(parent);
            cursor = parent.getParentId();
        }
    }
}
