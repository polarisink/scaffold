package github.polarisink.exception;

/**
 * Http Code
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
public enum HttpCode {
	OK(0),
	INTERNAL_ERROR(1),
	UNAUTHORIZED(2),
	USER_NOT_FOUND(3),
	PRODUCT_NOT_FOUND(4),
	INVALID_PARAMETER(5);

	private final Integer code;

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
