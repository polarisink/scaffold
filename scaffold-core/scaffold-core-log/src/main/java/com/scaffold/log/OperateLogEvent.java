package com.scaffold.log;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志事件
 *
 * @author Lion Li
 */

@Data
public class OperateLogEvent implements Serializable {

    /**
     * 操作模块
     */
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作人id
     */
    private String userId;
    /**
     * 操作人员
     */
    private String username;

    /**
     * 部门名称
     */
    private String orgName;

    /**
     * 请求url
     */
    private String url;

    /**
     * 操作地址
     */
    private String ip;

    /**
     * 请求参数
     */
    private String param;

    /**
     * 返回参数
     */
    private String result;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private LocalDateTime gmtCreated;

    /**
     * 消耗时间
     */
    private int costTime;
}
