package com.scaffold.base.exception;


import lombok.Getter;

/**
 * 基地例外
 * 基础异常类
 *
 * @author miaol
 * @date 2023/01/28
 */
@Getter
public class BaseException extends RuntimeException {


    /**
     * 异常消息参数
     */
    protected Object[] args;


    /**
     * 带消息的有参构造
     *
     * @param msg 味精
     */
    public BaseException(String msg) {
        super(msg);
    }

    /**
     * 无参构造
     */
    public BaseException() {
        super();
    }

    /**
     * 重写父类 fillInStackTrace 获取异常消息
     *
     * @return {@link Throwable}
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
