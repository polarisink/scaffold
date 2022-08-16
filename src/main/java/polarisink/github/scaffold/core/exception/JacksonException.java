package polarisink.github.scaffold.core.exception;


import polarisink.github.scaffold.core.IResponseEnum;

/**
 * jackson序列化异常
 *
 * @author lqs
 * @date 2022/3/21
 */
public class JacksonException extends BaseException {
  private static final long serialVersionUID = 140582950805588413L;

  public JacksonException(IResponseEnum responseEnum, Object[] args, String message) {
    super(responseEnum, args, message);
  }

  public JacksonException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
    super(responseEnum, args, message, cause);
  }

  public JacksonException(String msg) {
    super(msg);
  }

  public JacksonException(String format, Object... args) {
    super(format, args);
  }
}

