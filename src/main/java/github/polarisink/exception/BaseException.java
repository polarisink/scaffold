package github.polarisink.exception;

import github.polarisink.enums.ResponseEnum;

import java.util.Formatter;

/**
 * @author lqs
 */
public class BaseException extends RuntimeException {
	public final int code;
	public final String msg;

	public BaseException(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public BaseException(String msg) {
		this.code = ResponseEnum.CODE_9000.getCode();
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public BaseException(String format, Object... args) {
		String s = new Formatter().format(format, args).toString();
		this.code = ResponseEnum.CODE_9000.getCode();
		this.msg = s;
	}

}
