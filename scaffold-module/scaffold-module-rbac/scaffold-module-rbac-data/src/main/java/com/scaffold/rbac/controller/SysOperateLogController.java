package com.scaffold.rbac.controller;

import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.entity.SysOperateLog;
import com.scaffold.rbac.service.SysOperateLogService;
import com.scaffold.rbac.vo.log.SysOperateLogPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log/operation")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "操作日志查询和清理接口")
public class SysOperateLogController {
    private final SysOperateLogService service;

    @PostMapping("/page")
    @Operation(summary = "操作日志分页")
    public PageResponse<SysOperateLog> page(@RequestBody SysOperateLogPageVO vo) {
        return service.page(vo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除操作日志")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空操作日志")
    public void clean() {
        service.clean();
    }
}
