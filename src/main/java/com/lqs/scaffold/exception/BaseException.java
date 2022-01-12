package com.lqs.scaffold.exception;

import com.lqs.scaffold.enums.HttpCode;

/**
 * @author lqs
 * @describe 基本异常, 继承RuntimeException可被aop拦截
 * @date 2021/11/6
 */
public abstract class BaseException extends Exception {

	private final String lang;
	private final HttpCode httpCode;

	protected BaseException(String lang, HttpCode httpCode) {
		this.lang = lang;
		this.httpCode = httpCode;
	}

	public String getLang() {
		return lang;
	}

	public HttpCode getHttpCode() {
		return httpCode;
	}

}
