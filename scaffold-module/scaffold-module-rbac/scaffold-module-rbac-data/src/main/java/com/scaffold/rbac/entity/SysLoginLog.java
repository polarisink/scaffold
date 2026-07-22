package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseLongAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
@Entity
@Table(name = "sys_login_log", indexes = {
        @Index(name = "idx_login_log_username", columnList = "username"),
        @Index(name = "idx_login_log_created", columnList = "gmt_created")
})
@TableName("sys_login_log")
public class SysLoginLog extends BaseLongAuditable {

    @Comment("用户ID")
    private Long userId;
    @Column(length = 64)
    @Comment("登录账号")
    private String username;
    @Column(nullable = false, length = 20)
    @Comment("认证动作")
    private String action;
    @Column(length = 64)
    @Comment("客户端IP")
    private String ip;
    @Column(length = 500)
    @Comment("浏览器User-Agent")
    private String userAgent;
    @Column(nullable = false)
    @Comment("是否成功")
    private Boolean status = true;
    @Column(length = 500)
    @Comment("结果消息")
    private String message;
}
