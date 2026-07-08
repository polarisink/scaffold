package com.scaffold.codegen.entity;

import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "gen_table")
public class GenTableEntity extends BaseAuditable {

    @Column(nullable = false, length = 128)
    @Comment("数据库表名")
    private String tableName;

    @Comment("表注释")
    private String tableComment;

    @Column(nullable = false, length = 128)
    @Comment("Java 类名")
    private String className;

    @Column(nullable = false, length = 64)
    @Comment("业务模块名")
    private String moduleName;

    @Column(nullable = false, length = 128)
    @Comment("业务名")
    private String businessName;

    @Column(nullable = false)
    @Comment("Java 包名")
    private String packageName;

    @Column(nullable = false, length = 64)
    @Comment("作者")
    private String author;

    @Column(nullable = false, length = 32)
    @Comment("数据库类型")
    private String databaseType;

    @Column(length = 128)
    @Comment("数据库 schema")
    private String schemaName;

    @Column(nullable = false)
    @Comment("前端源码目录")
    private String frontendPath;

    @Column(nullable = false)
    @Comment("后端源码目录")
    private String backendPath;

    @Column(nullable = false, length = 128)
    @Comment("菜单名称")
    private String menuName;

    @Column(nullable = false)
    @Comment("菜单路由")
    private String menuPath;

    @Column(nullable = false, length = 128)
    @Comment("权限前缀")
    private String permissionPrefix;

    @Column(nullable = false, length = 64)
    @Comment("默认角色编码")
    private String defaultRoleCode = "admin";

    @Column(nullable = false)
    @Comment("兼容旧版：是否生成建表 SQL")
    private Boolean generateTable = false;

    @Column(nullable = false)
    @Comment("兼容旧版：是否生成菜单权限 SQL")
    private Boolean generateMenu = false;

}
