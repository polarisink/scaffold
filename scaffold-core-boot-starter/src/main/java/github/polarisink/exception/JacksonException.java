package github.polarisink.exception;

/**
 * @author lqs
 * @date 2022/3/19
 */
public class JacksonException extends BaseException {

	public JacksonException(String format, Object... args) {
		super(format, args);
	}
}
