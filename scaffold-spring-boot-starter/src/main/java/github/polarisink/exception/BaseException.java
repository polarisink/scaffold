package github.polarisink.exception;

import github.polarisink.enums.LangTypes;
import github.polarisink.enums.ResponseEnum;

import java.util.Formatter;

/**
 * Base Exception
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
public abstract class BaseException extends RuntimeException {

	private final String lang;
	private final Integer code;
	private final String msg;
	private HttpCode httpCode;

	protected BaseException(String lang, Integer code, String msg) {
		this.lang = lang;
		this.code = code;
		this.msg = msg;
	}

	protected BaseException(String lang,HttpCode httpCode){
		this.lang = lang;
		this.code = httpCode.getCode();
		//todo 完善
		this.msg = httpCode.getLabel();
	}

	protected BaseException(String format,Object... args){
		this.lang = LangTypes.ZH_CN.name();
		String s = new Formatter().format(format, args).toString();
		this.code = ResponseEnum.CODE_9000.getCode();
		this.msg = s;
	}

	public String getLang() {
		return lang;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public HttpCode getHttpCode() {
		return httpCode;
	}
}
