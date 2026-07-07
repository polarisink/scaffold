package com.scaffold.rbac.controller;

import com.scaffold.base.util.PageResponse;
import com.scaffold.base.util.R;
import com.scaffold.rbac.entity.SysDictData;
import com.scaffold.rbac.service.SysDictDataService;
import com.scaffold.rbac.vo.dict.SysDictDataCreateVO;
import com.scaffold.rbac.vo.dict.SysDictDataPageVO;
import com.scaffold.rbac.vo.dict.SysDictDataUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dict/data")
@RequiredArgsConstructor
@Tag(name = "字典数据", description = "字典选项管理及业务查询接口")
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    @PostMapping("/page")
    @Operation(summary = "字典数据分页")
    public PageResponse<SysDictData> page(@RequestBody SysDictDataPageVO pageVO) {
        return dictDataService.page(pageVO);
    }

    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型查询启用选项")
    public List<SysDictData> listByType(@PathVariable String dictType) {
        return dictDataService.listByType(dictType);
    }

    @PostMapping
    @Operation(summary = "新增字典数据")
    public R<Long> save(@RequestBody @Valid SysDictDataCreateVO createVO) {
        return R.success(dictDataService.save(createVO));
    }

    @PutMapping
    @Operation(summary = "修改字典数据")
    public void update(@RequestBody @Valid SysDictDataUpdateVO updateVO) {
        dictDataService.updateById(updateVO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典数据")
    public void delete(@PathVariable Long id) {
        dictDataService.deleteById(id);
    }
}
