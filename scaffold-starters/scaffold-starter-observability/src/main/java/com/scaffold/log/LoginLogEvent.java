package com.scaffold.log;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginLogEvent implements Serializable {

    private Long userId;
    private String username;
    private Integer status = BusinessStatus.SUCCESS.ordinal();
}
