package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 字典类型表。
 */
@Data
@Entity
@Table(name = "sys_dict_type")
@TableName("sys_dict_type")
public class SysDictType extends BaseAuditable {

    @Column(columnDefinition = "varchar(100) not null comment '字典名称'")
    private String dictName;

    @Column(columnDefinition = "varchar(100) not null comment '字典类型'", unique = true)
    private String dictType;

    @Column(columnDefinition = "bool not null default true comment '是否启用'")
    private Boolean status = true;

    @Column(columnDefinition = "varchar(255) comment '备注'")
    private String remark;
}
