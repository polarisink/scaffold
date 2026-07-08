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
@Table(name = "gen_column")
public class GenColumnEntity extends BaseAuditable {

    @Column(nullable = false)
    @Comment("生成表 ID")
    private Long tableId;

    @Column(nullable = false, length = 128)
    @Comment("数据库列名")
    private String columnName;

    @Column(nullable = false, length = 128)
    @Comment("Java 属性名")
    private String propertyName;

    @Column(nullable = false, length = 64)
    @Comment("JDBC 类型")
    private String jdbcType;

    @Column(nullable = false, length = 128)
    @Comment("数据库字段类型")
    private String columnType;

    @Column(nullable = false, length = 128)
    @Comment("Java 类型")
    private String javaType;

    @Column(nullable = false, length = 64)
    @Comment("TypeScript 类型")
    private String tsType;

    @Comment("字段注释")
    private String columnComment;

    @Comment("字段长度")
    private Integer columnLength;

    @Comment("数字精度")
    private Integer numericPrecision;

    @Comment("数字小数位")
    private Integer numericScale;

    @Column(nullable = false)
    @Comment("是否可空")
    private Boolean nullable = true;

    @Column(nullable = false)
    @Comment("是否主键")
    private Boolean primaryKey = false;

    @Column(nullable = false)
    @Comment("是否自增")
    private Boolean autoIncrement = false;

    @Column(nullable = false)
    @Comment("是否唯一")
    private Boolean uniqueKey = false;

    @Column(nullable = false)
    @Comment("是否查询字段")
    private Boolean queryable = false;

    @Column(nullable = false, length = 16)
    @Comment("查询方式")
    private String queryType = "EQ";

    @Column(nullable = false)
    @Comment("列表显示")
    private Boolean listVisible = true;

    @Column(nullable = false)
    @Comment("表单显示")
    private Boolean formVisible = true;

    @Column(nullable = false, length = 64)
    @Comment("表单控件")
    private String formWidget = "Input";

    @Column(length = 128)
    @Comment("字典类型")
    private String dictType;

    @Column(nullable = false)
    @Comment("排序")
    private Integer sortNo = 0;
}
