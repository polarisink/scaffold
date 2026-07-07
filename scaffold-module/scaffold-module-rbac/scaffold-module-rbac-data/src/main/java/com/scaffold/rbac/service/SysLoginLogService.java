package com.scaffold.rbac.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.entity.SysLoginLog;
import com.scaffold.rbac.mapper.SysLoginLogMapper;
import com.scaffold.rbac.vo.log.SysLoginLogPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SysLoginLogService {
    private final SysLoginLogMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<SysLoginLog> page(SysLoginLogPageVO vo) {
        return mapper.page(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void clean() {
        mapper.delete(Wrappers.<SysLoginLog>lambdaQuery());
    }
}
