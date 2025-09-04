package com.scaffold.biz.module.rbac.contant;

import com.scaffold.core.base.exception.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum RbacResultEnum implements Assert {

    USER_NOT_FOUND("用户{1}不存在"),
    UNIQUE_MENU_NAME("菜单名字不能重复"),
    UNIQUE_USERNAME("用户名不能重复"),
    CAN_NOT_DELETE_PARENT_MENU_NODE("不能删除有子菜单的菜单"),
    CAN_NOT_DELETE_PARENT_ORG_NODE("不能删除有下级组织的组织"),
    CAN_NOT_DELETE_ORG_HAS_USER("该组织下还有人员未移除"),
    UNIQUE_ORG_NAME("组织名不能重复"),
    MENU_URL_NOT_FOUND("路由地址不可为空"),
    UNIQUE_ROLE_NAME("角色名不能重复"),
    UNIQUE_ROLE_CODE("角色名不能重复"),
    PASSWD_NOT_CHANGE("密码未修改"),
    CAN_NOT_DELETE_ROLE_HAS_USER("不能删除有用户的角色"),
    CAN_NOT_BAN_MYSELF("不能封禁自己");


    /**
     * 返回消息
     */
    private final String message;
}


