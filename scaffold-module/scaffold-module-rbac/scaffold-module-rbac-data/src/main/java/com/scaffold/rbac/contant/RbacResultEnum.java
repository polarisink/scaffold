package com.scaffold.rbac.contant;

import com.scaffold.base.exception.Assert;
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
    UNIQUE_ORG_CODE("组织编码不能重复"),
    ORG_NOT_FOUND("组织不存在"),
    ORG_PARENT_NOT_FOUND("上级组织不存在"),
    ORG_TREE_CYCLE("组织层级不能形成循环"),
    MENU_URL_NOT_FOUND("路由地址不可为空"),
    UNIQUE_ROLE_NAME("角色名不能重复"),
    UNIQUE_ROLE_CODE("角色名不能重复"),
    PASSWD_NOT_CHANGE("密码未修改"),
    CAN_NOT_DELETE_ROLE_HAS_USER("不能删除有用户的角色"),
    CAN_NOT_BAN_MYSELF("不能封禁自己"),
    CONFIG_NOT_FOUND("系统配置不存在"),
    UNIQUE_CONFIG_NAME("配置名称不能重复"),
    UNIQUE_CONFIG_KEY("配置键不能重复"),
    CAN_NOT_DELETE_SYSTEM_CONFIG("系统内置配置不能删除"),
    CAN_NOT_MODIFY_SYSTEM_CONFIG_KEY("系统内置配置不能修改配置键"),
    DICT_TYPE_NOT_FOUND("字典类型不存在"),
    DICT_DATA_NOT_FOUND("字典数据不存在"),
    UNIQUE_DICT_NAME("字典名称不能重复"),
    UNIQUE_DICT_TYPE("字典类型不能重复"),
    UNIQUE_DICT_LABEL("同一字典类型下标签不能重复"),
    UNIQUE_DICT_VALUE("同一字典类型下字典值不能重复"),
    CAN_NOT_DELETE_DICT_HAS_DATA("请先删除该类型下的字典数据");


    /**
     * 返回消息
     */
    private final String message;
}
