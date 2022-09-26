package github.polarisink.common.asserts;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>参数校验异常返回结果</p>
 *
 * @author aries
 * @date 2022/5/2
 */
@Getter
@RequiredArgsConstructor
public enum ArgumentE implements ArgumentExceptionAssert {
  /**
   * 绑定参数校验异常
   */
  BASE(9000, "参数校验基本异常"),

  /**
   * 参数校验异常
   */
  VALID_ERROR(9000, "参数校验异常"),

  ;

  /**
   * 返回码
   */
  private final int code;
  /**
   * 返回消息
   */
  private final String message;

}
