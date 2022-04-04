package github.polarisink.enums;

import lombok.Getter;

/**
 * 返回状态泛型
 */
@Getter
public enum ResponseEnum {

	CODE_200(200, "message.common.success"),
	CODE_9000(9000, "inside.system.error"),
	PARAM_ERROR(9001, "inside.param.error");

	private Integer code;
	private String message;

	ResponseEnum(Integer code, String value) {
		this.code = code;
		this.message = value;
	}

}
