package com.scaffold.rbac.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.entity.SysOperateLog;
import com.scaffold.rbac.mapper.SysOperateLogMapper;
import com.scaffold.rbac.vo.log.SysOperateLogPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysOperateLogService {
    private final SysOperateLogMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<SysOperateLog> page(SysOperateLogPageVO vo) {
        return mapper.page(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void clean() {
        mapper.delete(Wrappers.<SysOperateLog>lambdaQuery());
    }
}
