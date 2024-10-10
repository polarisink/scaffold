package com.scaffold.core.base.util;

import cn.hutool.http.HttpStatus;
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
    /**
     * 状态码
     */
    private int code = HttpStatus.HTTP_OK;
    /**
     * 提示信息
     */
    private String msg;
    /**
     * 业务数据
     */
    private T data;

    private R() {
    }


    /**
     * 构造器
     *
     * @param data 数据
     * @param code 代码
     * @param msg  味精
     */
    private R(T data, Integer code, String msg) {
        if (null != data) {
            setData(data);
        }
        if (null != code) {
            setCode(code);
        }
        if (null != msg) {
            setMsg(msg);
        }

    }

    /**
     * 成功
     *
     * @param data 数据
     * @param msg  味精
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> success(T data, String msg) {
        return of(data, HttpStatus.HTTP_OK, msg);
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
     * @param data 数据
     * @param code 代码
     * @param msg  味精
     * @return {@link R}<{@link T}>
     */
    private static <T> R<T> of(T data, Integer code, String msg) {
        return new R<>(data, code, msg);
    }

    /**
     * 失败
     *
     * @param msg 味精
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> failed(String msg) {
        return of(null, HttpStatus.HTTP_VERSION, msg);
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
        return of(null, HttpStatus.HTTP_VERSION, null);
    }

    /**
     * 失败
     *
     * @param code 代码
     * @return {@link R}<{@link T}>
     */
    public static <T> R<T> failed(Integer code) {
        return of(null, code, null);
    }

    /**
     * 返回成功
     */
    public static <T> R<T> success() {

        return new R<>(null, HttpStatus.HTTP_OK, null);
    }

}
