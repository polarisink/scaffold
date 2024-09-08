package com.scaffold.biz.module.rbac.vo.menu;

import com.scaffold.biz.module.rbac.entity.SysMenu;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * 系统功能表(SysMenu)转换接口
 *
 * @author makejava
 * @since 2024-07-26 11:10:02
 */
@Mapper
public interface SysMenuConvert {

    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);

    SysMenu convert(SysMenuCreateVO vo);

    SysMenu convert(SysMenuUpdateVO vo);

    SysMenuResultVO convert(SysMenu vo);

    List<SysMenuResultVO> convert(List<SysMenu> vo);

    List<SysMenuResultVO> convert(Collection<SysMenu> menuList);
}

