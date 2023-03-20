package github.polarisink.dao.wrapper;

import lombok.Data;

import static github.polarisink.common.asserts.BaseE.SUCCESS;

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
        this.code = SUCCESS.getCode();
        this.message = SUCCESS.getMessage();
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

    public static <T> Response<T> of(int code, String message, T data) {
        return new Response<>(code, message, data);
    }

    public static <T> Response<T> fail(int code, String message) {
        return of(code, message, null);
    }
}
