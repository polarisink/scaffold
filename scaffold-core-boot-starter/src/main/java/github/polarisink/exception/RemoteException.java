package github.polarisink.exception;

/**
 * Remote Service Exception
 *
 * @author Bill
 * @version 1.0
 * @since 2020-09-05
 */
public class RemoteException extends BaseException {

	protected RemoteException(String lang, Integer code, String msg) {
		super(lang, code, msg);
	}

	protected RemoteException(String lang, HttpCode httpCode) {
		super(lang, httpCode);
	}

	protected RemoteException(String format, Object... args) {
		super(format, args);
	}
}
