package polarisink.github.scaffold.core.exception;


import polarisink.github.scaffold.core.IResponseEnum;

/**
 * <p>校验异常</p>
 * <p>调用接口时，参数格式不合法可以抛出该异常</p>
 *
 * @author aries
 * @date 2022/5/2
 */
public class ValidationException extends BaseException {


  private static final long serialVersionUID = 7908303374834207999L;

  public ValidationException(IResponseEnum responseEnum, Object[] args, String message) {
    super(responseEnum, args, message);
  }

  public ValidationException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
    super(responseEnum, args, message, cause);
  }

  public ValidationException(String msg) {
    super(msg);
  }

  public ValidationException(String format, Object... args) {
    super(format, args);
  }
}
