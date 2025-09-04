package com.scaffold.biz.module.rbac.vo.role;

import com.scaffold.biz.module.rbac.entity.SysRole;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 角色(SysRole)转换接口
 *
 * @author aries
 * @since 2024-07-22 20:38:41
 */
@Mapper
public interface SysRoleConvert {

    SysRoleConvert INSTANCE = Mappers.getMapper(SysRoleConvert.class);

    /**
     * 新建时使用
     *
     * @param vo
     * @return 实体
     */
    SysRole convert(SysRoleCreateVO vo);

    /**
     * 返回单个结果使用
     *
     * @param vo
     * @return 实体
     */
    SysRoleResultVO convert(SysRole vo);


    /**
     * 返回多个结果使用
     *
     * @param vo
     * @return 实体
     */
    List<SysRoleResultVO> convert(List<SysRole> vo);
}

