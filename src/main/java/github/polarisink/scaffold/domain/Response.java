package github.polarisink.scaffold.domain;

import github.polarisink.scaffold.infrastructure.asserts.AssertConst;
import lombok.Data;

/**
 * <p>通用返回结果</p>
 *
 * @author aries
 * @date 2019/5/2
 */
@Data
public class Response<T> {

    private final int code;
    /**
     * 返回消息
     */
    private final String message;
    /**
     * 数据列表
     */
    private final T data;

    public Response(T data) {
        this.code = AssertConst.SUCCESS_CODE;
        this.message = AssertConst.SUCCESS_MESSAGE;
        this.data = data;
    }

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> of(T data) {
        return new Response<>(data);
    }

    public static <T> Response<T> of() {
        return new Response<>(null);
    }

    public static <T> Response<T> of(int code, String message, T data) {
        return new Response<>(code, message, data);
    }

    public static <T> Response<T> fail(int code, String message) {
        return of(code, message, null);
    }

    public static <T> Response<T> fail(String message) {
        return of(AssertConst.BUSINESS_ERROR_CODE, message, null);
    }

}
