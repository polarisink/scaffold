package github.polarisink.exception;

/**
 * Bad Request Exception
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
public class BadRequestException extends Exception {
  public BadRequestException() {
    super();
  }

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public BadRequestException(Throwable cause) {
    super(cause);
  }

  protected BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
