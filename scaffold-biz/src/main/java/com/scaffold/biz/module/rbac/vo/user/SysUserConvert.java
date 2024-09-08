package com.scaffold.biz.module.rbac.vo.user;

import com.scaffold.biz.module.rbac.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * (SysUser)转换接口
 *
 * @author aries
 * @since 2024-07-22 20:40:08
 */
@Mapper
public interface SysUserConvert {

    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    /**
     * 新建时使用
     *
     * @param vo
     * @return 实体
     */
    SysUser convert(SysUserCreateVO vo);
}

