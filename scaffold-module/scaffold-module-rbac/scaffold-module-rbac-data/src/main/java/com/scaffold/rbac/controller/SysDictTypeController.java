package com.scaffold.rbac.controller;

import com.scaffold.base.util.PageResponse;
import com.scaffold.rbac.entity.SysDictType;
import com.scaffold.rbac.service.SysDictTypeService;
import com.scaffold.rbac.vo.dict.SysDictTypeCreateVO;
import com.scaffold.rbac.vo.dict.SysDictTypePageVO;
import com.scaffold.rbac.vo.dict.SysDictTypeUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dict/type")
@RequiredArgsConstructor
@Tag(name = "字典类型", description = "字典类型管理接口")
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    @PostMapping("/page")
    @Operation(summary = "字典类型分页")
    public PageResponse<SysDictType> page(@RequestBody SysDictTypePageVO pageVO) {
        return dictTypeService.page(pageVO);
    }

    @GetMapping("/options")
    @Operation(summary = "启用的字典类型选项")
    public List<SysDictType> options() {
        return dictTypeService.options();
    }

    @PostMapping
    @Operation(summary = "新增字典类型")
    public Long save(@RequestBody @Valid SysDictTypeCreateVO createVO) {
        return dictTypeService.save(createVO);
    }

    @PutMapping
    @Operation(summary = "修改字典类型")
    public void update(@RequestBody @Valid SysDictTypeUpdateVO updateVO) {
        dictTypeService.updateById(updateVO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典类型")
    public void delete(@PathVariable Long id) {
        dictTypeService.deleteById(id);
    }
}
