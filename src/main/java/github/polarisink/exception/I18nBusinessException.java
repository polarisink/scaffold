package github.polarisink.exception;


import github.polarisink.enums.HttpCode;

/**
 * 包含国际化的业务异常
 *
 * @author lqs
 */
public class I18nBusinessException {
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

	public I18nBusinessException(HttpCode code) {
		this.code = code;
	}

	public I18nBusinessException(String lang, HttpCode code) {
		this.lang = lang;
		this.code = code;
	}
}
