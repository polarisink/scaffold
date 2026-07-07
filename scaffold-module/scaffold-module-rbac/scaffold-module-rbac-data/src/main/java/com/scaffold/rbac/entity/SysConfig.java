package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

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
    @Column(columnDefinition = "varchar(255) not null comment '参数名字'", unique = true)
    private String configName;

    /**
     * 参数键名
     */
    @Column(columnDefinition = "varchar(255) not null comment '参数键名'", unique = true)
    private String configKey;

    /**
     * 参数键值
     */
    @Column(columnDefinition = "varchar(255) not null comment '参数键值'")
    private String configValue;

    /**
     * 是否是系统内置
     */
    @Column(columnDefinition = "bool not null comment '是否是系统内置'")
    private Boolean sysFlag = false;

    /**
     * 备注
     */
    @Column(columnDefinition = "varchar(255) comment '备注'")
    private String remark;


}
