package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Comment;

/**
 * 字典类型表。
 */
@Data
@Entity
@Table(name = "sys_dict_type")
@TableName("sys_dict_type")
public class SysDictType extends BaseAuditable {

    @Column(nullable = false, length = 100)
    @Comment("字典名称")
    private String dictName;

    @Column(nullable = false, length = 100, unique = true)
    @Comment("字典类型")
    private String dictType;

    @Column(nullable = false)
    @Comment("是否启用")
    private Boolean status = true;

    @Comment("备注")
    private String remark;
}
