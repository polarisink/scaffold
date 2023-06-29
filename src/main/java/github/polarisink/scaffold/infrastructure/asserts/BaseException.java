package github.polarisink.scaffold.infrastructure.asserts;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import lombok.Getter;

/**
 * <p>基础异常类，所有自定义异常类都需要继承本类</p>
 *
 * @author aries
 * @date 2022/5/2
 */
@Getter
public class BaseException extends RuntimeException implements Serializable {
    protected int code;
    protected String message;

    public BaseException(int code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public BaseException(String msg) {
        super(msg);
        this.code = AssertConst.BUSINESS_ERROR_CODE;
        this.message = msg;
    }

    public BaseException(String format, Object... args) {
        this.code = AssertConst.BUSINESS_ERROR_CODE;
        this.message = StrUtil.format(format, args);
    }
}
