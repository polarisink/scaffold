package github.polarisink.exception;


/**
 * @author polaris
 * @date 2021/3/15 3:42 下午
 */
public class BusinessException extends RuntimeException {

  private String lang;

  private HttpCode code;

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public HttpCode getCode() {
    return code;
  }

  public void setCode(HttpCode code) {
    this.code = code;
  }

  public BusinessException(HttpCode code) {
    this.code = code;
  }

  public BusinessException(String lang, HttpCode code) {
    this.lang = lang;
    this.code = code;
  }


}
