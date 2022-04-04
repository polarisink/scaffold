package github.polarisink.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	private final Boolean useHttpCode;
	/**
	 * 可选项90op9
	 */
	private HttpCode httpCode;

	protected BaseException(String lang, Integer code, String msg) {
		this.lang = lang;
		this.code = code;
		this.useHttpCode = false;
		this.msg = msg;
	}

	protected BaseException(String lang, HttpCode httpCode) {
		this.lang = lang;
		this.code = httpCode.getCode();
		this.useHttpCode = true;
		this.msg = httpCode.getLabel();
	}

	protected BaseException(String format, Object... args) {
		this.lang = LangTypes.ZH_CN.name();
		String s = new Formatter().format(format, args).toString();
		this.code = ResponseEnum.CODE_9000.getCode();
		this.msg = s;
		this.useHttpCode = false;
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

	public Boolean getUseHttpCode() {
		return useHttpCode;
	}

	public HttpCode getHttpCode() {
		return useHttpCode ? httpCode : null;
	}
}
