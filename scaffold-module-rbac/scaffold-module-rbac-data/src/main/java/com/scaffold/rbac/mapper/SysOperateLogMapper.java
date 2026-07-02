package com.scaffold.rbac.mapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.base.util.PageResponse;
import com.scaffold.orm.MyBaseMapper;
import com.scaffold.orm.starter.PageUtils;
import com.scaffold.rbac.entity.SysOperateLog;
import com.scaffold.rbac.vo.log.SysOperateLogPageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysOperateLogMapper extends MyBaseMapper<SysOperateLog> {

    default PageResponse<SysOperateLog> page(SysOperateLogPageVO vo) {
        Page<SysOperateLog> page = selectPage(new Page<>(vo.getPageNo(), vo.getPageSize()),
                Wrappers.<SysOperateLog>lambdaQuery()
                        .like(StrUtil.isNotBlank(vo.getTitle()), SysOperateLog::getTitle, vo.getTitle())
                        .like(StrUtil.isNotBlank(vo.getOperator()), SysOperateLog::getOperator, vo.getOperator())
                        .eq(vo.getStatus() != null, SysOperateLog::getStatus, vo.getStatus())
                        .orderByDesc(SysOperateLog::getGmtCreated));
        return PageUtils.of(page);
    }

    default List<SysOperateLog> findByBizNoAndType(String bizNo, String type, String subType) {
        return selectList(Wrappers.<SysOperateLog>lambdaQuery()
                .eq(StrUtil.isNotBlank(bizNo), SysOperateLog::getBizNo, bizNo)
                .eq(StrUtil.isNotBlank(type), SysOperateLog::getTitle, type)
                .eq(StrUtil.isNotBlank(subType), SysOperateLog::getBusinessType, subType)
                .orderByDesc(SysOperateLog::getGmtCreated));
    }
}
