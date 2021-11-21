package com.lqs.scaffold.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lqs.scaffold.constants.Constants;
import com.lqs.scaffold.enums.HttpCode;
import com.lqs.scaffold.exception.BadRequestException;

import java.io.IOException;

/**
 * Result Wrapper
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
public class ResultT<T> {
	private Integer result;
	private T data;
	private String message;

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static <T> ResultT<T> of(String lang, HttpCode code, T data)
		throws BadRequestException {
		ResultT<T> resultT = new ResultT<>();
		resultT.setResult(code.getCode());
		resultT.setData(data);
		String message;
		try {
			message = LangProvider.getInstance(lang).getMessage(code.getLabel());
		} catch (IOException e) {
			throw new BadRequestException(e);
		}
		resultT.setMessage(message);
		return resultT;
	}

	public static <T> ResultT<T> of(String lang, HttpCode code)
		throws BadRequestException {
		return of(lang, code, null);
	}

	public static <T> ResultT<T> of(HttpCode code, String message) {
		ResultT<T> resultT = new ResultT<>();
		resultT.setResult(code.getCode());
		resultT.setMessage(message);
		return resultT;
	}

	public static <T> ResultT<T> success(String lang, T data)
		throws BadRequestException {
		return of(lang, HttpCode.OK, data);
	}

	public static <T> ResultT<T> success(T data)
		throws BadRequestException {
		return of(Constants.DEFAULT_LANG, HttpCode.OK, data);
	}

}
