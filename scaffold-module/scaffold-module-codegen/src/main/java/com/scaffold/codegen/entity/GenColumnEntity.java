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
@Table(name = "gen_column")
public class GenColumnEntity extends BaseAuditable {

    @Column(columnDefinition = "bigint not null comment '生成表 ID'")
    private Long tableId;

    @Column(columnDefinition = "varchar(128) not null comment '数据库列名'")
    private String columnName;

    @Column(columnDefinition = "varchar(128) not null comment 'Java 属性名'")
    private String propertyName;

    @Column(columnDefinition = "varchar(64) not null comment 'JDBC 类型'")
    private String jdbcType;

    @Column(columnDefinition = "varchar(128) not null comment '数据库字段类型'")
    private String columnType;

    @Column(columnDefinition = "varchar(128) not null comment 'Java 类型'")
    private String javaType;

    @Column(columnDefinition = "varchar(64) not null comment 'TypeScript 类型'")
    private String tsType;

    @Column(columnDefinition = "varchar(255) comment '字段注释'")
    private String columnComment;

    @Column(columnDefinition = "int comment '字段长度'")
    private Integer columnLength;

    @Column(columnDefinition = "int comment '数字精度'")
    private Integer numericPrecision;

    @Column(columnDefinition = "int comment '数字小数位'")
    private Integer numericScale;

    @Column(columnDefinition = "bit not null default 1 comment '是否可空'")
    private Boolean nullable = true;

    @Column(columnDefinition = "bit not null default 0 comment '是否主键'")
    private Boolean primaryKey = false;

    @Column(columnDefinition = "bit not null default 0 comment '是否自增'")
    private Boolean autoIncrement = false;

    @Column(columnDefinition = "bit not null default 0 comment '是否唯一'")
    private Boolean uniqueKey = false;

    @Column(columnDefinition = "bit not null default 0 comment '是否查询字段'")
    private Boolean queryable = false;

    @Column(columnDefinition = "varchar(16) not null default 'EQ' comment '查询方式'")
    private String queryType = "EQ";

    @Column(columnDefinition = "bit not null default 1 comment '列表显示'")
    private Boolean listVisible = true;

    @Column(columnDefinition = "bit not null default 1 comment '表单显示'")
    private Boolean formVisible = true;

    @Column(columnDefinition = "varchar(64) not null default 'Input' comment '表单控件'")
    private String formWidget = "Input";

    @Column(columnDefinition = "varchar(128) comment '字典类型'")
    private String dictType;

    @Column(columnDefinition = "int not null default 0 comment '排序'")
    private Integer sortNo = 0;
}
