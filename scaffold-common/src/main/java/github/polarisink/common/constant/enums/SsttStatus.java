package github.polarisink.common.constant.enums;

/**
 * @author aries
 * @date 2022/7/15
 */
public enum SsttStatus {
  /**
   * 开始
   */
  START(0, "补偿前"),
  /**
   * 后面的
   */
  END(1, "补偿后");

  private final int code;
  private final String name;

  SsttStatus(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static SsttStatus getStatus(int code) {
    return code == START.code ? START : END;
  }

  public String getName() {
    return name;
  }

  public int getCode() {
    return code;
  }

}
