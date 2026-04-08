package com.scaffold.bizlog.component;

import lombok.Data;

@Data
public class UserInfo {
    private Long userId;
    private String username;
    private String phone;
    private Integer age;
}