package github.polarisink.scaffold.common.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单类型枚举类
 *
 * @author aries
 * @date 2022/6/24
 */
@Getter
@RequiredArgsConstructor
public enum MenuTypes implements BaseEnum {
  DIRECTORY(1, "目录"), MENU(2, "菜单"), BUTTON(3, "按钮"),
  ;
  private static final Map<Integer, MenuTypes> CODE_MAP = new HashMap<>();
  private static final Map<String, MenuTypes> NAME_MAP = new HashMap<>();

  static {
    for (MenuTypes menuTypes : values()) {
      CODE_MAP.put(menuTypes.getType(), menuTypes);
      NAME_MAP.put(menuTypes.getName(), menuTypes);
    }
  }

  private final Integer type;
  private final String name;

}
