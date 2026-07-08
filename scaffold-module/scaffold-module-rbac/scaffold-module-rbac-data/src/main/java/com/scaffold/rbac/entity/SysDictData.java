package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Comment;

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

    @Column(nullable = false, length = 100)
    @Comment("字典类型")
    private String dictType;

    @Column(nullable = false, length = 100)
    @Comment("字典标签")
    private String dictLabel;

    @Column(nullable = false, length = 100)
    @Comment("字典值")
    private String dictValue;

    @Column(nullable = false)
    @Comment("显示排序")
    private Integer dictSort = 0;

    @Column(nullable = false)
    @Comment("是否启用")
    private Boolean status = true;

    @Column(nullable = false)
    @Comment("是否默认选项")
    private Boolean defaultFlag = false;

    @Column(length = 20)
    @Comment("标签样式")
    private String tagType;

    @Comment("备注")
    private String remark;
}
