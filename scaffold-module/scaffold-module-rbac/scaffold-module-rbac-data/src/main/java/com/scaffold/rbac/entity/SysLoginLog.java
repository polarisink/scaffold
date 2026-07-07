package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "sys_login_log", indexes = {
        @Index(name = "idx_login_log_username", columnList = "username"),
        @Index(name = "idx_login_log_created", columnList = "gmt_created")
})
@TableName("sys_login_log")
public class SysLoginLog extends BaseAuditable {

    @Column(columnDefinition = "bigint comment '用户ID'")
    private Long userId;
    @Column(columnDefinition = "varchar(64) comment '登录账号'")
    private String username;
    @Column(columnDefinition = "varchar(20) not null comment '认证动作'")
    private String action;
    @Column(columnDefinition = "varchar(64) comment '客户端IP'")
    private String ip;
    @Column(columnDefinition = "varchar(500) comment '浏览器User-Agent'")
    private String userAgent;
    @Column(columnDefinition = "bool not null default true comment '是否成功'")
    private Boolean status = true;
    @Column(columnDefinition = "varchar(500) comment '结果消息'")
    private String message;
}
