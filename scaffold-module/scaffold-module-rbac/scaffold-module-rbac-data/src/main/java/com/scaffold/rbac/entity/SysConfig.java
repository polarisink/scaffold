package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Comment;

/**
 * 参数配置表 sys_config
 *
 * @author aries
 */
@Data
@TableName("sys_config")
@Table(name = "sys_config")
@Entity
public class SysConfig extends BaseAuditable {

    /**
     * 参数名称
     */
    @Column(nullable = false, unique = true)
    @Comment("参数名字")
    private String configName;

    /**
     * 参数键名
     */
    @Column(nullable = false, unique = true)
    @Comment("参数键名")
    private String configKey;

    /**
     * 参数键值
     */
    @Column(nullable = false)
    @Comment("参数键值")
    private String configValue;

    /**
     * 是否是系统内置
     */
    @Column(nullable = false)
    @Comment("是否是系统内置")
    private Boolean sysFlag = false;

    /**
     * 备注
     */
    @Comment("备注")
    private String remark;


}
