package github.polarisink.enums;

/**
 * @author lqs
 * @describe
 * @date 2021/11/6
 */
public enum LangTypes {
	/**
	 *
	 */
	EN_US(0),
	/**
	 *
	 */
	ZH_CN(6),
	/**
	 *
	 */
	ZH_TW(7);

	private final int code;

	LangTypes(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
