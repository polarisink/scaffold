package github.polarisink.common.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lqs
 * @date 2022/2/21
 */
@Getter
@RequiredArgsConstructor
public enum DataTypes implements BaseEnum {
  /**
   * 调试
   */
  Debug(0, "Optimization", "调试"),
  /**
   * 补偿
   */
  Compensation(1, "Compensation", "补偿"),
  /**
   * 健康保障
   */
  Diagnose(2, "Diagnose", "健康保障"),

  /**
   * 装配
   */
  Assembly(3, "Assembly", "装配"),
  ;
  private static final Map<Integer, DataTypes> TYPE_MAP = new HashMap<>();
  private static final Map<String, DataTypes> TYPE_STR_MAP = new HashMap<>();

  static {
    for (DataTypes value : values()) {
      TYPE_MAP.put(value.getType(), value);
      TYPE_STR_MAP.put(value.getTypeStr(), value);
    }
  }

  private final Integer type;
  private final String typeStr;
  private final String name;

  public static Set<Integer> getAllCodes() {
    return TYPE_MAP.keySet();
  }

  public static DataTypes getType(Integer id) {
    return TYPE_MAP.get(id);
  }

  public static DataTypes getTypeByName(String name) {
    return TYPE_STR_MAP.get(name);
  }
}
