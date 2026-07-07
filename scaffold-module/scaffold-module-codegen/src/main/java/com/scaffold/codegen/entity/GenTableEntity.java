package com.scaffold.codegen.entity;

import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gen_table")
public class GenTableEntity extends BaseAuditable {

    @Column(columnDefinition = "varchar(128) not null comment '数据库表名'")
    private String tableName;

    @Column(columnDefinition = "varchar(255) comment '表注释'")
    private String tableComment;

    @Column(columnDefinition = "varchar(128) not null comment 'Java 类名'")
    private String className;

    @Column(columnDefinition = "varchar(64) not null comment '业务模块名'")
    private String moduleName;

    @Column(columnDefinition = "varchar(128) not null comment '业务名'")
    private String businessName;

    @Column(columnDefinition = "varchar(255) not null comment 'Java 包名'")
    private String packageName;

    @Column(columnDefinition = "varchar(64) not null comment '作者'")
    private String author;

    @Column(columnDefinition = "varchar(32) not null comment '数据库类型'")
    private String databaseType;

    @Column(columnDefinition = "varchar(128) comment '数据库 schema'")
    private String schemaName;

    @Column(columnDefinition = "varchar(255) not null comment '前端源码目录'")
    private String frontendPath;

    @Column(columnDefinition = "varchar(255) not null comment '后端源码目录'")
    private String backendPath;

    @Column(columnDefinition = "varchar(128) not null comment '菜单名称'")
    private String menuName;

    @Column(columnDefinition = "varchar(255) not null comment '菜单路由'")
    private String menuPath;

    @Column(columnDefinition = "varchar(128) not null comment '权限前缀'")
    private String permissionPrefix;

    @Column(columnDefinition = "varchar(64) not null default 'admin' comment '默认角色编码'")
    private String defaultRoleCode = "admin";

    @Column(columnDefinition = "bit(1) not null default b'0' comment '兼容旧版：是否生成建表 SQL'")
    private Boolean generateTable = false;

    @Column(columnDefinition = "bit(1) not null default b'0' comment '兼容旧版：是否生成菜单权限 SQL'")
    private Boolean generateMenu = false;

}
