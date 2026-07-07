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
@Table(name = "sys_operate_log", indexes = {
        @Index(name = "idx_operate_log_operator", columnList = "operator"),
        @Index(name = "idx_operate_log_created", columnList = "gmt_created")
})
@TableName("sys_operate_log")
public class SysOperateLog extends BaseAuditable {

    @Column(columnDefinition = "varchar(100) comment '模块标题'")
    private String title;
    @Column(columnDefinition = "varchar(50) comment '业务类型'")
    private String businessType;
    @Column(columnDefinition = "varchar(100) comment '业务编号'")
    private String bizNo;
    @Column(columnDefinition = "varchar(100) comment '操作人'")
    private String operator;
    @Column(columnDefinition = "varchar(500) comment '操作内容'")
    private String action;
    @Column(columnDefinition = "varchar(255) comment '调用方法'")
    private String method;
    @Column(columnDefinition = "varchar(16) comment '请求方法'")
    private String requestMethod;
    @Column(columnDefinition = "varchar(255) comment '请求地址'")
    private String url;
    @Column(columnDefinition = "varchar(64) comment '客户端IP'")
    private String ip;
    @Column(columnDefinition = "text comment '请求参数'")
    private String param;
    @Column(columnDefinition = "text comment '响应结果'")
    private String result;
    @Column(columnDefinition = "bool not null default true comment '是否成功'")
    private Boolean status = true;
    @Column(columnDefinition = "varchar(1000) comment '错误信息'")
    private String errorMsg;
    @Column(columnDefinition = "int not null default 0 comment '耗时毫秒'")
    private Integer costTime = 0;
    @Column(columnDefinition = "text comment '扩展信息'")
    private String extra;
}
