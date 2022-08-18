package github.polarisink.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lqs
 * 表单中数据的区分，如自检专检等
 * @date 2021/11/24
 */
public enum CheckTypes {

  ASSEMBLY(0, "装配"), SELF_CHECK(1, "自检"), SPECIAL_CHECK(2, "专检");

  private static final Map<Integer, CheckTypes> map = new HashMap<>();

  static {
    for (CheckTypes value : values()) {
      map.put(value.id, value);
    }
  }

  private Integer id;
  private String name;

  CheckTypes(Integer type, String name) {
    this.name = name;
    this.id = type;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

}
