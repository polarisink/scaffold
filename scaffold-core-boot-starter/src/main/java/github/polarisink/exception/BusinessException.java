package github.polarisink.exception;


/**
 * @author polaris
 * @date 2021/3/15 3:42 下午
 */
public class BusinessException extends BaseException {

	protected BusinessException(String lang, Integer code, String msg) {
		super(lang, code, msg);
	}

	protected BusinessException(String lang, HttpCode httpCode) {
		super(lang, httpCode);
	}

	protected BusinessException(String format, Object... args) {
		super(format, args);
	}
}
