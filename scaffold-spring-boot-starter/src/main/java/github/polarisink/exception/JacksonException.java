package github.polarisink.exception;

import github.polarisink.enums.ResponseEnum;

import java.util.Formatter;

/**
 * @author lqs
 * @date 2022/3/19
 */
public class JacksonException extends BaseException{

	public JacksonException(String format, Object... args) {
		super(format,args);
	}
}
