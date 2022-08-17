package github.polarisink.scaffold.common.exception;

/**
 * 只包装了 错误信息 的 {@link RuntimeException}.
 * 用于 Assert 中用于包装自定义异常信息
 *
 * @author aries
 * @date 2020/6/20
 */
public class WrapMessageException extends RuntimeException {

  private static final long serialVersionUID = 8675747258629665980L;

  public WrapMessageException(String message) {
    super(message);
  }

  public WrapMessageException(String message, Throwable cause) {
    super(message, cause);
  }
}
