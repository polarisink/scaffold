package github.polarisink.common.exception;


import github.polarisink.common.IResponseEnum;

/**
 * <p>业务异常</p>
 * <p>业务处理时，出现异常，可以抛出该异常</p>
 *
 * @author aries
 * @date 2022/5/2
 */
public class BusinessException extends BaseException {

  private static final long serialVersionUID = -1400868899661202155L;

  public BusinessException(IResponseEnum responseEnum, Object[] args, String message) {
    super(responseEnum, args, message);
  }

  public BusinessException(IResponseEnum responseEnum, Object[] args, String message, Throwable cause) {
    super(responseEnum, args, message, cause);
  }

  public BusinessException(String msg) {
    super(msg);
  }

  public BusinessException(String format, Object... args) {
    super(format, args);
  }

}