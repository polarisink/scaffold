package com.scaffold.biz.module.rbac.controller;

import com.scaffold.biz.module.rbac.entity.SysUser;
import com.scaffold.biz.module.rbac.service.SysUserService;
import com.scaffold.biz.module.rbac.vo.user.*;
import com.scaffold.core.base.util.PageResponse;
import com.scaffold.core.base.util.R;
import com.scaffold.core.log.annotation.Log;
import com.scaffold.core.log.vo.BusinessType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.scaffold.biz.module.rbac.contant.RbacLogConst.ROLE;
import static com.scaffold.biz.module.rbac.contant.RbacLogConst.USER;

/**
 * (SysUser)表控制层
 *
 * @author aries
 * @since 2024-07-22 20:40:07
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户", description = "接口")
public class SysUserController {

    private final SysUserService sysUserService;


    /**
     * 分页查询所有数据
     *
     * @param req 分页请求
     * @return 分页数据
     */
    @PostMapping("/page")
    @Operation(summary = "分页", description = "传入分页请求")
    public PageResponse<SysUser> page(@RequestBody SysUserPageVO req) {
        return sysUserService.page(req);
    }

    /**
     * 新增数据
     *
     * @param createVO 创建请求
     * @return id
     */
    @PostMapping
    @Log(title = ROLE, businessType = BusinessType.INSERT)
    @Operation(summary = "保存", description = "新增用户，传入用户信息及所属角色")
    public R<String> save(@RequestBody @Valid SysUserCreateVO createVO) {
        return R.success(sysUserService.save(createVO));
    }

    /**
     * 更新数据
     *
     * @param updateVO 更新请求
     */
    @PutMapping
    @Log(title = ROLE, businessType = BusinessType.UPDATE)
    @Operation(summary = "修改", description = "修改用户信息，这里不能修改密码")
    public void updateById(@RequestBody SysUserUpdateVO updateVO) {
        sysUserService.updateById(updateVO);
    }

    /**
     * 通过ID删除数据
     *
     * @param id 主键ID
     */
    @DeleteMapping("/{id}")
    @Log(title = ROLE, businessType = BusinessType.DELETE)
    @Operation(summary = "删除", description = "传入用户id")
    public void delete(@PathVariable("id") Long id) {
        sysUserService.deleteById(id);
    }

    /**
     * 用户用户权限树
     *
     * @return 菜单树
     */
    @Operation(summary = "用户信息")
    @GetMapping
    public SysUserInfo userInfo() {
        return sysUserService.userInfo();
    }

    /**
     * 用户更新密码
     *
     * @param passwdUpdateVO 更新密码请求
     * @return 成功
     */
    @Operation(summary = "用户更新密码")
    @Log(title = USER, businessType = BusinessType.UPDATE)
    @PostMapping("/passwd/update")
    public void updatePasswd(@RequestBody PasswdUpdateVO passwdUpdateVO) {
        sysUserService.updatePasswd(passwdUpdateVO);
    }

    /**
     * 重置用户密码
     * todo 加入注解校验
     *
     * @param userId 用户id
     */
    @Operation(summary = "重置用户密码")
    @Log(title = USER, businessType = BusinessType.UPDATE)
    @PostMapping("/passwd/reset/{userId}")
    public void resetPasswd(@PathVariable Long userId) {
        sysUserService.resetPasswd(userId);
    }

    /**
     * 禁用/启用用户
     *
     * @param userId 用户id
     */
    @PostMapping("/{userId}")
    @Operation(summary = "禁用/启用用户")
    @Log(title = USER, businessType = BusinessType.UPDATE)
    public void ban(@PathVariable Long userId) {
        sysUserService.ban(userId);
    }
}

