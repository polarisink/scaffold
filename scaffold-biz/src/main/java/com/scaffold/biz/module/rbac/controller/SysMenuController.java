package com.scaffold.biz.module.rbac.controller;

import com.scaffold.biz.module.rbac.entity.SysMenu;
import com.scaffold.biz.module.rbac.service.SysMenuService;
import com.scaffold.biz.module.rbac.vo.menu.SysMenuCreateVO;
import com.scaffold.biz.module.rbac.vo.menu.SysMenuUpdateVO;
import com.scaffold.core.base.util.R;
import com.scaffold.core.log.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.scaffold.biz.module.rbac.contant.RbacLogConst.MENU;

/**
 * 菜单(SysMenu)表控制层
 *
 * @author aries
 * @since 2024-07-22 20:38:39
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "菜单", description = "菜单接口")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 获取所有菜单树
     *
     * @return 菜单树
     */
    @GetMapping("/tree")
    @Operation(summary = "菜单树结构")
    public R<List<SysMenu>> tree() {
        return R.success(sysMenuService.tree());
    }

    /**
     * 新增数据
     *
     * @param createVO 创建请求
     * @return id
     */
    @PostMapping
    @Log(title = MENU, businessType = INSERT)
    @Operation(summary = "保存", description = "传入sysMenu")
    public R<String> save(@RequestBody @Valid SysMenuCreateVO createVO) {
        return R.success(sysMenuService.save(createVO));
    }

    /**
     * 更新数据
     *
     * @param updateVO 更新请求
     */
    @Log(title = MENU, businessType = UPDATE)
    @PutMapping
    @Operation(summary = "修改", description = "传入sysMenu")
    public R<Void> updateById(@RequestBody @Valid SysMenuUpdateVO updateVO) {
        sysMenuService.updateById(updateVO);
        return R.success();
    }

    /**
     * 通过ID删除数据
     *
     * @param id 主键ID
     */
    @Log(title = MENU, businessType = DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "删除", description = "传入id")
    public R<Void> delete(@PathVariable("id") Long id) {
        sysMenuService.deleteById(id);
        return R.success();
    }
}

