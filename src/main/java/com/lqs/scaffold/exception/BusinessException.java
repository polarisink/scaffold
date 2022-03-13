package com.lqs.scaffold.exception;


/**
 * @author lqs
 */
public class BusinessException extends BaseException {

    public BusinessException(int code, String msg) {
        super(code, msg);
    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String format, Object... args) {
        super(format, args);
    }
}
