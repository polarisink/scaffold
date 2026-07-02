package com.scaffold.rbac.controller;

import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.entity.SysLoginLog;
import com.scaffold.rbac.service.SysLoginLogService;
import com.scaffold.rbac.vo.log.SysLoginLogPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log/login")
@RequiredArgsConstructor
@Tag(name = "登录日志", description = "登录日志查询和清理接口")
public class SysLoginLogController {
    private final SysLoginLogService service;

    @PostMapping("/page")
    @Operation(summary = "登录日志分页")
    public PageResponse<SysLoginLog> page(@RequestBody SysLoginLogPageVO vo) {
        return service.page(vo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除登录日志")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空登录日志")
    public void clean() {
        service.clean();
    }
}
