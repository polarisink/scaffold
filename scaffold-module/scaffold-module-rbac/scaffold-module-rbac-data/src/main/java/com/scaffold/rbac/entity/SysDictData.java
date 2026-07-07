package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 字典数据表。使用 dictType 关联字典类型，方便业务代码通过稳定编码直接查询。
 */
@Data
@Entity
@Table(name = "sys_dict_data", indexes = {
        @Index(name = "idx_sys_dict_data_type", columnList = "dict_type"),
        @Index(name = "idx_sys_dict_data_type_sort", columnList = "dict_type,dict_sort")
})
@TableName("sys_dict_data")
public class SysDictData extends BaseAuditable {

    @Column(columnDefinition = "varchar(100) not null comment '字典类型'")
    private String dictType;

    @Column(columnDefinition = "varchar(100) not null comment '字典标签'")
    private String dictLabel;

    @Column(columnDefinition = "varchar(100) not null comment '字典值'")
    private String dictValue;

    @Column(columnDefinition = "int not null default 0 comment '显示排序'")
    private Integer dictSort = 0;

    @Column(columnDefinition = "bool not null default true comment '是否启用'")
    private Boolean status = true;

    @Column(columnDefinition = "bool not null default false comment '是否默认选项'")
    private Boolean defaultFlag = false;

    @Column(columnDefinition = "varchar(20) comment '标签样式'")
    private String tagType;

    @Column(columnDefinition = "varchar(255) comment '备注'")
    private String remark;
}
