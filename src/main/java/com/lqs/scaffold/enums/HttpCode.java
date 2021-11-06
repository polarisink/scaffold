package com.lqs.scaffold.enums;

/**
 * @author lqs
 * @describe
 * @date 2021/11/6
 */
public enum HttpCode {
	/**
	 *
	 */
	OK(10000),
	/**
	 * 没有该用户
	 */
	NO_SUCH_USER(20000);
	private final int code;

	HttpCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getLabel() {
		return "code." + this.toString().toLowerCase().replaceAll("_", ".");
	}

}
