package com.scaffold.log;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OperateLogEvent implements Serializable {

    private String title;
    private Integer businessType;
    private String method;
    private String requestMethod;
    private String userId;
    private String username;
    private String orgName;
    private String url;
    private String ip;
    private String param;
    private String result;
    private Integer status;
    private String errorMsg;
    private LocalDateTime gmtCreated;
    private int costTime;
}
