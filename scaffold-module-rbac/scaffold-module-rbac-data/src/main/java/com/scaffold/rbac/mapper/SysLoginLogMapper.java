package com.scaffold.rbac.mapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import com.scaffold.rbac.entity.SysLoginLog;
import com.scaffold.rbac.vo.log.SysLoginLogPageVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLoginLogMapper extends MyBaseMapper<SysLoginLog> {

    default PageResponse<SysLoginLog> page(SysLoginLogPageVO vo) {
        Page<SysLoginLog> page = selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysLoginLog>lambdaQuery()
                        .like(StrUtil.isNotBlank(vo.getUsername()), SysLoginLog::getUsername, vo.getUsername())
                        .like(StrUtil.isNotBlank(vo.getIp()), SysLoginLog::getIp, vo.getIp())
                        .eq(StrUtil.isNotBlank(vo.getAction()), SysLoginLog::getAction, vo.getAction())
                        .eq(vo.getStatus() != null, SysLoginLog::getStatus, vo.getStatus())
                        .orderByDesc(SysLoginLog::getGmtCreated));
        return PageUtils.of(page);
    }
}
