package com.scaffold.rbac.controller;

import com.scaffold.base.util.PageResponse;
import com.scaffold.base.util.R;
import com.scaffold.log.BusinessType;
import com.scaffold.log.Log;
import com.scaffold.rbac.entity.SysConfig;
import com.scaffold.rbac.service.SysConfigService;
import com.scaffold.rbac.vo.config.SysConfigCreateVO;
import com.scaffold.rbac.vo.config.SysConfigPageVO;
import com.scaffold.rbac.vo.config.SysConfigUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.scaffold.rbac.contant.RbacLogConst.CONFIG;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
@Tag(name = "系统配置", description = "系统参数配置接口")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @PostMapping("/page")
    @Operation(summary = "配置分页")
    public PageResponse<SysConfig> page(@RequestBody SysConfigPageVO pageVO) {
        return sysConfigService.page(pageVO);
    }

    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据配置键查询")
    public SysConfig findByKey(@PathVariable String configKey) {
        return sysConfigService.findByKey(configKey);
    }

    @PostMapping
    @Log(title = CONFIG, businessType = BusinessType.INSERT)
    @Operation(summary = "新增配置")
    public R<Long> save(@RequestBody @Valid SysConfigCreateVO createVO) {
        return R.success(sysConfigService.save(createVO));
    }

    @PutMapping
    @Log(title = CONFIG, businessType = BusinessType.UPDATE)
    @Operation(summary = "修改配置")
    public void update(@RequestBody @Valid SysConfigUpdateVO updateVO) {
        sysConfigService.updateById(updateVO);
    }

    @DeleteMapping("/{id}")
    @Log(title = CONFIG, businessType = BusinessType.DELETE)
    @Operation(summary = "删除配置")
    public void delete(@PathVariable Long id) {
        sysConfigService.deleteById(id);
    }
}
