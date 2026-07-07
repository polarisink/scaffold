package com.scaffold.base.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * api统一返回对象
 *
 * @author aries
 * @date 2022/01/14
 */
@Data
@AllArgsConstructor
public class R<T> implements Serializable {
    public static final int SUCCESS_CODE = 0;
    public static final int SYSTEM_ERROR_CODE = 50000;

    /**
     * 状态码
     */
    private int code = SUCCESS_CODE;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 业务数据
     */
    private T data;

    private R() {
    }


    /**
     * 构造器
     *
     * @param data    数据
     * @param code    代码
     * @param message 提示信息
     */
    private R(T data, Integer code, String message) {
        if (null != data) {
            setData(data);
        }
        if (null != code) {
            setCode(code);
        }
        if (null != message) {
            setMessage(message);
        }

    }

    /**
     * 成功
     *
     * @param data    数据
     * @param message 提示信息
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> success(T data, String message) {
        return of(data, SUCCESS_CODE, message);
    }

    /**
     * 成功
     *
     * @param data 数据
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> success(T data) {
        return of(data, null, null);
    }

    /**
     * @param data    数据
     * @param code    代码
     * @param message 提示信息
     * @return {@link R}<{@link T}>
     */
    private static <T> R<T> of(T data, Integer code, String message) {
        return new R<>(data, code, message);
    }

    /**
     * 失败
     *
     * @param message 提示信息
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> failed(String message) {
        return of(null, SYSTEM_ERROR_CODE, message);
    }


    /**
     * 失败的
     * 返回自定义错误
     *
     * @param code    代码
     * @param message 消息
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> failed(Integer code, String message) {
        return of(null, code, message);
    }

    /**
     * 失败
     *
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> failed() {
        return of(null, SYSTEM_ERROR_CODE, null);
    }

    /**
     * 返回成功
     */
    public static <T> R<T> success() {
        return new R<>(null, SUCCESS_CODE, null);
    }

}
