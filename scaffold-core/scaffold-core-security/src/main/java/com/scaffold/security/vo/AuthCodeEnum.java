package com.scaffold.security.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scaffold.base.exception.IResponseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AuthCodeEnum implements IResponseEnum {
    ACCESS_DENIED(403, "禁止访问"),
    UNAUTHORIZED(443, "未授权"),
    AUTHENTICATION_FAILED(444, "认证失败"),
    LOGIN_FAILED(445, "登录失败"),
    ;

    private final int code;
    private final String message;
}
