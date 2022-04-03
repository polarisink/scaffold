package github.polarisink.exception;

/**
 * Remote Service Exception
 *
 * @author Bill
 * @version 1.0
 * @since 2020-09-05
 */
public class RemoteServiceException extends Exception {

  public RemoteServiceException() {
    super();
  }

  public RemoteServiceException(String message) {
    super(message);
  }

  public RemoteServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public RemoteServiceException(Throwable cause) {
    super(cause);
  }

  protected RemoteServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
