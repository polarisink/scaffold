package com.scaffold.rbac.controller;

import com.scaffold.base.util.PageResponse;
import com.scaffold.log.BusinessType;
import com.scaffold.log.Log;
import com.scaffold.rbac.entity.SysRole;
import com.scaffold.rbac.service.SysRoleService;
import com.scaffold.rbac.vo.menu.SysRoleWrapper;
import com.scaffold.rbac.vo.role.SysRoleCreateVO;
import com.scaffold.rbac.vo.role.SysRolePageVO;
import com.scaffold.rbac.vo.role.SysRoleUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.scaffold.rbac.contant.RbacLogConst.ROLE;


/**
 * 角色(SysRole)表控制层
 *
 * @author aries
 * @since 2024-07-22 20:38:40
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "角色", description = "角色接口")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 分页查询所有数据
     *
     * @param req 分页请求
     * @return 分页数据
     */
    @PostMapping("/page")
    @Operation(summary = "分页", description = "传入分页请求")
    public PageResponse<SysRole> page(@RequestBody SysRolePageVO req) {
        return sysRoleService.page(req);
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键ID
     * @return 单条数据
     */
    @GetMapping("/{id}")
    @Operation(summary = "详情", description = "传入id，获取觉得的菜单树")
    public SysRoleWrapper roleWrapper(@PathVariable Long id) {
        return sysRoleService.roleWrapper(id);
    }

    /**
     * 新增数据
     *
     * @param createVO 创建请求
     * @return id
     */
    @PostMapping
    @Log(title = ROLE, businessType = BusinessType.INSERT)
    @Operation(summary = "保存", description = "传入sysRole")
    public void save(@RequestBody @Valid SysRoleCreateVO createVO) {
        sysRoleService.save(createVO);
    }

    /**
     * 更新数据
     *
     * @param updateVO 更新请求
     */
    @PutMapping
    @Log(title = ROLE, businessType = BusinessType.UPDATE)
    @Operation(summary = "修改", description = "传入sysRole")
    public void updateById(@RequestBody @Valid SysRoleUpdateVO updateVO) {
        sysRoleService.updateById(updateVO);
    }

    /**
     * 通过ID删除数据
     *
     * @param id 主键ID
     */
    @DeleteMapping("/{id}")
    @Log(title = ROLE, businessType = BusinessType.DELETE)
    @Operation(summary = "删除", description = "传入id")
    public void delete(@PathVariable Long id) {
        sysRoleService.deleteById(id);
    }
}

