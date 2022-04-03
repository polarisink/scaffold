package github.polarisink.core;import github.polarisink.enums.ResponseEnum;import io.swagger.v3.oas.annotations.media.Schema;import lombok.Data;import lombok.ToString;import java.io.Serializable;@Data@ToString@Schema(name = "返回结果集")public class ResultT implements Serializable {    @Schema(name = "是否成功")    private Boolean success;    @Schema(name = "消息")    private String message;    @Schema(name = "返回码")    private Integer code;    @Schema(name = "返回结果集")    private Object data;    public ResultT(Boolean success, String message, Integer code, Object data) {        this.success = success;        this.message = message;        this.code = code;        this.data = data;    }    public ResultT() {    }    public static ResultT success(Boolean success, String message, Integer code, Object data) {        return new ResultT(success, message, code, data);    }    public static ResultT success(String message, Object data) {        return success(true, message, ResponseEnum.CODE_200.getCode(), data);    }    public static ResultT success(Object data) {        return success(true, ResponseEnum.CODE_200.getMessage(), ResponseEnum.CODE_200.getCode(), data);    }    public static ResultT success() {        return success(true, ResponseEnum.CODE_200.getMessage(), ResponseEnum.CODE_200.getCode(), null);    }    public static ResultT fail(Boolean success, String message, Integer code) {        return new ResultT(success, message, code, null);    }    public static ResultT fail(String message) {        return success(false, message, ResponseEnum.CODE_9000.getCode(), null);    }    public static ResultT fail(ResponseEnum responseEnum) {        return success(false, responseEnum.getMessage(), responseEnum.getCode(), null);    }    public static ResultT fail(String message, Integer code) {        return success(false, message, code, null);    }    public static ResultT fail() {        return success(false, ResponseEnum.CODE_9000.getMessage(), ResponseEnum.CODE_9000.getCode(), null);    }}