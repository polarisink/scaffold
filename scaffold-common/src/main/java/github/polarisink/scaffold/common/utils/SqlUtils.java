package github.polarisink.scaffold.common.utils;

/**
 * sql工具
 *
 * @author aries
 * @date 2022/5/11
 */
public class SqlUtils {
  /**
   * 获取模糊查询字符串
   *
   * @param pattern
   * @return
   */
  public static String getBlurStr(String pattern) {
    return "%" + pattern + "%";
  }
}
