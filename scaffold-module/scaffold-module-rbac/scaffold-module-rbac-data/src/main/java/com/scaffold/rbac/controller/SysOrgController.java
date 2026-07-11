package com.scaffold.rbac.controller;

import com.scaffold.rbac.entity.SysOrg;
import com.scaffold.rbac.service.SysOrgService;
import com.scaffold.rbac.vo.org.SysOrgCreateVO;
import com.scaffold.rbac.vo.org.SysOrgUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
@Tag(name = "组织", description = "组织机构接口")
public class SysOrgController {

    private final SysOrgService sysOrgService;

    @GetMapping("/tree")
    @Operation(summary = "组织树")
    public List<SysOrg> tree() {
        return sysOrgService.tree();
    }

    @PostMapping
    @Operation(summary = "新增组织")
    public Long save(@RequestBody @Valid SysOrgCreateVO createVO) {
        return sysOrgService.save(createVO);
    }

    @PutMapping
    @Operation(summary = "修改组织")
    public void update(@RequestBody @Valid SysOrgUpdateVO updateVO) {
        sysOrgService.updateById(updateVO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除组织")
    public void delete(@PathVariable Long id) {
        sysOrgService.deleteById(id);
    }
}
