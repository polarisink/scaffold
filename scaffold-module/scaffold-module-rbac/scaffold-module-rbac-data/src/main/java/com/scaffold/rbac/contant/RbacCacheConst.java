package com.scaffold.rbac.contant;

/**
 * 缓存常量
 */
public interface RbacCacheConst {
    //用户菜单树缓存
    String USER_TREE_CACHE = "rbac:user:tree";
    //用户角色code缓存
    String USER_ROLES_CACHE = "rbac:user:roles";
    //用户权限列表缓存
    String USER_PERMISSIONS_CACHE = "rbac:user:permissions";
    //菜单树
    String MENU_TREE_CACHE = "rbac:menu:tree";
    //角色菜单树
    String ROLE_TREE_CACHE = "rbac:role:tree";
    //组织树
    String ORG_TREE_CACHE = "rbac:org:tree";
    //字典数据缓存
    String DICT_DATA_CACHE = "rbac:dict:data";
    //配置缓存
    String CONFIG_DATA_CACHE = "rbac:config:data";

}
