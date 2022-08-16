package polarisink.github.scaffold.core.enums;

import polarisink.github.scaffold.core.assertion.BaseExceptionAssert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author aries
 * @date 2022/5/20
 */
@Getter
@RequiredArgsConstructor
public enum BaseE implements BaseExceptionAssert {

  /**
   *
   */
  BASE(9000, "基本异常"),

  SUCCESS(200, "SUCCESS"),
  /**
   * 公共基本异常
   */
  /**
   * 服务器繁忙，请稍后重试
   */
  SERVER_BUSY(9000, "服务器繁忙"),
  /**
   * 服务器异常，无法识别的异常，尽可能对通过判断减少未定义异常抛出
   */
  SERVER_ERROR(9000, "服务器异常"),

  /**
   * 5***，一般对应于{@link polarisink.github.scaffold.core.exception.ArgumentException}，系统封装的工具出现异常
   */

  // Time
  DATE_NOT_NULL(9000, "日期不能为空"), DATETIME_NOT_NULL(5001, "时间不能为空"), TIME_NOT_NULL(5001, "时间不能为空"), DATE_PATTERN_MISMATCH(5002, "日期[{}]与格式[{}]不匹配，无法解析"), PATTERN_NOT_NULL(5003, "日期格式不能为空"), PATTERN_INVALID(5003, "日期格式[{}]无法识别"),
  ;
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
