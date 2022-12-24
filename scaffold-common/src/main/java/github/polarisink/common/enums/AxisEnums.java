package github.polarisink.common.enums;

import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 轴的方向,现在最多的7轴,因此直接写在枚举类中 0代表整机
 *
 * @author lqs
 * @date 2022/2/23
 */
@Getter
@ToString
@RequiredArgsConstructor
public enum AxisEnums {
  /**
   * X
   */
  X(1, "X轴", 0),
  /**
   * Y
   */
  Y(2, "Y轴", 1),
  /**
   * Z
   */
  Z(3, "Z轴", 2),
  /**
   * A
   */
  A(4, "A轴", 3),
  /**
   * C
   */
  C(5, "C轴", 4),
  /**
   * S轴就是主轴
   */
  S(6, "S轴", 5),
  /**
   * 刀库
   */
  ToolMagazine(7, "刀库", 6);

  private static final List<AxisEnums> VALUES = Arrays.asList(values());
  private static final Map<Integer, AxisEnums> CODE_MAP = new HashMap<>();
  private static final Map<Integer, AxisEnums> SORT_MAP = new HashMap<>();
  private static final Map<String, AxisEnums> NAME_MAP = new HashMap<>();

  static {
    for (AxisEnums axisEnums : VALUES) {
      CODE_MAP.put(axisEnums.getCode(), axisEnums);
      SORT_MAP.put(axisEnums.getSort(), axisEnums);
      NAME_MAP.put(axisEnums.getAxis(), axisEnums);
    }
  }

  /**
   * code码,做区分
   */
  private final Integer code;
  /**
   * 轴名
   */
  private final String axis;
  /**
   * 排序
   */
  private final Integer sort;

  public static AxisEnums getAxis(String name) {
    AxisEnums a = null;
    if (StrUtil.isBlank(name)) {
      return a;
    }
    for (Map.Entry<String, AxisEnums> entry : NAME_MAP.entrySet()) {
      String key = entry.getKey();
      AxisEnums axisEnums = entry.getValue();
      if (key.contains(name.substring(0, 1))) {
        a = axisEnums;
        break;
      }
    }
    return a;
  }

  public static AxisEnums getAxis(Integer code) {
    return CODE_MAP.get(code);
  }

  public static AxisEnums getAxisBySort(Integer sort) {
    return SORT_MAP.get(sort);
  }

  public static Integer getMaxSort() {
    return VALUES.size();
  }

  public static Set<Integer> getAllAxisCodes() {
    return CODE_MAP.keySet();
  }
}
