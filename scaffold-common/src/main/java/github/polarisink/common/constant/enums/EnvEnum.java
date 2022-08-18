package github.polarisink.common.constant.enums;

import java.util.Arrays;

/**
 * 运行环境枚举
 *
 * @author aries
 * @date 2022/7/15
 */
public enum EnvEnum {
  /**
   * 开发环境
   */
  DEV("dev"),
  /**
   * 测试环境
   */
  TEST("test"),
  /**
   * 生产环境
   */
  PROD("prod");
  private String env;

  EnvEnum(String name) {
    this.env = name;
  }

  public static EnvEnum getInstance(String envName) {
    return Arrays.stream(values()).filter(enums -> enums.getEnv().equals(envName)).findFirst().orElse(DEV);
  }

  public String getEnv() {
    return env;
  }
}
