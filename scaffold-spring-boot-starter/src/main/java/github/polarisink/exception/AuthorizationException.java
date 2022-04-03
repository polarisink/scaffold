package github.polarisink.exception;

/**
 * Authorization Exception
 *
 * @author Bill
 * @version 1.0
 * @since 2020-08-31
 */
public class AuthorizationException extends BaseException {

  public AuthorizationException(String lang) {
    super(lang, HttpCode.UNAUTHORIZED);
  }

}
