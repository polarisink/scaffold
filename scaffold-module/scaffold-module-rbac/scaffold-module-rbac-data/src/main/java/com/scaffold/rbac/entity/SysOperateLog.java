package com.scaffold.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scaffold.orm.BaseAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
@Entity
@Table(name = "sys_operate_log", indexes = {
        @Index(name = "idx_operate_log_operator", columnList = "operator"),
        @Index(name = "idx_operate_log_created", columnList = "gmt_created")
})
@TableName("sys_operate_log")
public class SysOperateLog extends BaseAuditable {

    @Column(length = 100)
    @Comment("模块标题")
    private String title;
    @Column(length = 50)
    @Comment("业务类型")
    private String businessType;
    @Column(length = 100)
    @Comment("业务编号")
    private String bizNo;
    @Column(length = 100)
    @Comment("操作人")
    private String operator;
    @Column(length = 500)
    @Comment("操作内容")
    private String action;
    @Comment("调用方法")
    private String method;
    @Column(length = 16)
    @Comment("请求方法")
    private String requestMethod;
    @Comment("请求地址")
    private String url;
    @Column(length = 64)
    @Comment("客户端IP")
    private String ip;
    @Column(length = 4000)
    @Comment("请求参数")
    private String param;
    @Column(length = 4000)
    @Comment("响应结果")
    private String result;
    @Column(nullable = false)
    @Comment("是否成功")
    private Boolean status = true;
    @Column(length = 1000)
    @Comment("错误信息")
    private String errorMsg;
    @Column(nullable = false)
    @Comment("耗时毫秒")
    private Integer costTime = 0;
    @Column(length = 4000)
    @Comment("扩展信息")
    private String extra;
}
