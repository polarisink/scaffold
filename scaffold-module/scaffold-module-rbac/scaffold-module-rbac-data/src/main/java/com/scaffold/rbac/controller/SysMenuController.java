package com.scaffold.rbac.controller;

import com.scaffold.rbac.entity.SysMenu;
import com.scaffold.rbac.service.SysMenuService;
import com.scaffold.rbac.vo.menu.SysMenuCreateVO;
import com.scaffold.rbac.vo.menu.SysMenuUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public List<SysMenu> tree() {
        return sysMenuService.tree();
    }

    /**
     * 新增数据
     *
     * @param createVO 创建请求
     * @return id
     */
    @PostMapping
    @Operation(summary = "保存", description = "传入sysMenu")
    public Long save(@RequestBody @Valid SysMenuCreateVO createVO) {
        return sysMenuService.save(createVO);
    }

    /**
     * 更新数据
     *
     * @param updateVO 更新请求
     */
    @PutMapping
    @Operation(summary = "修改", description = "传入sysMenu")
    public void updateById(@RequestBody @Valid SysMenuUpdateVO updateVO) {
        sysMenuService.updateById(updateVO);
    }

    /**
     * 通过ID删除数据
     *
     * @param id 主键ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除", description = "传入id")
    public void delete(@PathVariable Long id) {
        sysMenuService.deleteById(id);
    }
}
