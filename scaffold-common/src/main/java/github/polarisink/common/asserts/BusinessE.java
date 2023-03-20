package github.polarisink.common.asserts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author aries
 * @date 2022/5/20
 */
@Getter
@RequiredArgsConstructor
public enum BusinessE implements BusinessExceptionAssert {

    /**
     *
     */
    BASE(9000, "基本业务异常"),

    /**
     *
     */
    NO_SUCH_STEP(9000, "没有该步骤"),
    /**
     *
     */
    ALREADY_EXISTS(9000, "已存在"),
    ;

    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String message;


}
