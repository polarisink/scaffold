package github.polarisink.core;

import github.polarisink.enums.LangTypes;
import github.polarisink.exception.BadRequestException;
import github.polarisink.exception.BaseException;
import github.polarisink.exception.HttpCode;
import lombok.Data;

import java.io.IOException;


/**
 * Result Wrapper
 *
 * @author polarisink
 * @since 2022-04-03
 */
@Data
public class ResultWrapper<T> {
	private Integer code;
	private T data;
	private String msg;

	/**
	 * specify code and msg myself
	 *
	 * @param code
	 * @param msg
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> ResultWrapper<T> success(Integer code, String msg, T data) {
		ResultWrapper<T> wrapper = new ResultWrapper<>();
		wrapper.setCode(code);
		wrapper.setMsg(msg);
		wrapper.setData(data);
		return wrapper;
	}

	/**
	 * use lang and httpCode
	 *
	 * @param lang
	 * @param httpCode
	 * @param data
	 * @param <T>
	 * @return
	 * @throws BadRequestException
	 */
	public static <T> ResultWrapper<T> success(String lang, HttpCode httpCode, T data) throws BadRequestException {
		ResultWrapper<T> wrapper = new ResultWrapper<>();
		wrapper.setCode(HttpCode.OK.getCode());
		String message;
		try {
			message = LangProvider.getInstance(lang).getMessage(httpCode.getLabel());
		} catch (IOException e) {
			throw new BadRequestException(e);
		}
		wrapper.setMsg(message);
		wrapper.setData(data);
		return wrapper;
	}

	/**
	 * only specify data
	 *
	 * @param data
	 * @param <T>
	 * @return
	 * @throws BadRequestException
	 */
	public static <T> ResultWrapper<T> success(T data) throws BadRequestException {
		return success(LangTypes.getDefaultLang().name(), HttpCode.OK, data);
	}

	/**
	 * fail with code and msg
	 * @param code
	 * @param msg
	 * @return
	 */
	public static ResultWrapper<Void> fail(Integer code, String msg) {
		ResultWrapper<Void> wrapper = new ResultWrapper<>();
		wrapper.setCode(code);
		wrapper.setMsg(msg);
		return wrapper;
	}

	/**
	 * fail with httpCode and msg
	 * @param httpCode
	 * @param msg
	 * @return
	 */
	public static ResultWrapper<Void> fail(HttpCode httpCode, String msg) {
		ResultWrapper<Void> wrapper = new ResultWrapper<>();
		wrapper.setCode(httpCode.getCode());
		wrapper.setMsg(msg);
		return wrapper;
	}

	/**
	 * fail with lang and httpCode
	 * @param lang
	 * @param httpCode
	 * @return
	 * @throws BadRequestException
	 */
	public static ResultWrapper<Void> fail(String lang, HttpCode httpCode) throws BadRequestException {
		return success(lang, httpCode, null);
	}

	/**
	 * fail with baseException
	 * @param be
	 * @return
	 * @throws BadRequestException
	 */
	public static ResultWrapper<Void> fail(BaseException be) throws BadRequestException {
		return be.getUseHttpCode() ? fail(be.getLang(), be.getHttpCode()) : fail(be.getCode(), be.getMsg());
	}


}
