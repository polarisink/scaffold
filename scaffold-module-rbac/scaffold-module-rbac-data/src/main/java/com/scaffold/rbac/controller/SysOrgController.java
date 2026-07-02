package com.scaffold.rbac.controller;

import com.scaffold.base.util.R;
import com.scaffold.log.BusinessType;
import com.scaffold.log.Log;
import com.scaffold.rbac.entity.SysOrg;
import com.scaffold.rbac.service.SysOrgService;
import com.scaffold.rbac.vo.org.SysOrgCreateVO;
import com.scaffold.rbac.vo.org.SysOrgUpdateVO;
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

import java.util.List;

import static com.scaffold.rbac.contant.RbacLogConst.ORG;

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
    @Log(title = ORG, businessType = BusinessType.INSERT)
    @Operation(summary = "新增组织")
    public R<Long> save(@RequestBody @Valid SysOrgCreateVO createVO) {
        return R.success(sysOrgService.save(createVO));
    }

    @PutMapping
    @Log(title = ORG, businessType = BusinessType.UPDATE)
    @Operation(summary = "修改组织")
    public void update(@RequestBody @Valid SysOrgUpdateVO updateVO) {
        sysOrgService.updateById(updateVO);
    }

    @DeleteMapping("/{id}")
    @Log(title = ORG, businessType = BusinessType.DELETE)
    @Operation(summary = "删除组织")
    public void delete(@PathVariable Long id) {
        sysOrgService.deleteById(id);
    }
}
