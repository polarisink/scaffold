package github.polarisink.dao.annotation;

import org.springframework.core.annotation.AliasFor;

/**
 * @author aries
 * @date 2022/12/12
 */
public @interface TdEntity {

  @AliasFor("value")
  String name();

  @AliasFor("name")
  String value();

  /**
   * 初始化表语句
   * @return
   */
  String init();
}
