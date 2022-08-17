package github.polarisink.scaffold.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录日志的类型枚举
 *
 * @author hzsk
 */
@Getter
@AllArgsConstructor
public enum LoginLogTypeEnum implements BaseEnum {

  /**
   * 使用账号登录
   */
  LOGIN_USERNAME(100, "账号登录"),
  /**
   * 使用社交登录
   */
  //LOGIN_SOCIAL(101, "社交登录"),
  /**
   * 使用手机登陆
   */
  LOGIN_MOBILE(103, "手机验证码登陆"),


  /**
   * 自己主动登出
   */
  LOGOUT_SELF(200, "主动登出"),
  /**
   * 强制退出
   */
  LOGOUT_DELETE(202, "强制退出"),
  ;

  /**
   * 日志类型
   */
  private final Integer type;
  private final String name;
}
