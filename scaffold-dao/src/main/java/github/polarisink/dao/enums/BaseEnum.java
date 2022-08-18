package github.polarisink.dao.enums;

/**
 * 枚举类基类
 *
 * @author aries
 * @date 2022/8/9
 */
public interface BaseEnum {
  /**
   * 获取类型
   *
   * @return
   */
  Integer getType();

  /**
   * 获取类型描述
   *
   * @return
   */
  String getName();
}
