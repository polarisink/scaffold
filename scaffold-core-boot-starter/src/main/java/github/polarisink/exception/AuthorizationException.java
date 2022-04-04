package github.polarisink.exception;

/**
 * Authorization Exception
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
public class AuthorizationException extends BaseException {
	protected AuthorizationException(String lang, Integer code, String msg) {
		super(lang, code, msg);
	}

	protected AuthorizationException(String lang, HttpCode httpCode) {
		super(lang, httpCode);
	}

	protected AuthorizationException(String format, Object... args) {
		super(format, args);
	}
}
