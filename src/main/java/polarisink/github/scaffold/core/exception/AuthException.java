package polarisink.github.scaffold.core.exception;


import polarisink.github.scaffold.core.IResponseEnum;

/**
 * <p>鉴权异常</p>
 * <p>鉴权处理时，出现异常，可以抛出该异常</p>
 *
 * @author aries
 * @date 2022/5/2
 */
public class AuthException extends BaseException {

  private static final long serialVersionUID = 443287115214800317L;

  public AuthException(IResponseEnum responseEnum, Object[] args, String message) {
    super(responseEnum, args, message);
  }

  public AuthException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
    super(responseEnum, args, message, cause);
  }

  public AuthException(String msg) {
    super(msg);
  }

  public AuthException(String format, Object... args) {
    super(format, args);
  }

}