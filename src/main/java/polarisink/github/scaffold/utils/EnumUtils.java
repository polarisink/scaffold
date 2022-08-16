package polarisink.github.scaffold.utils;


import polarisink.github.scaffold.bean.dto.Kv;
import polarisink.github.scaffold.bean.enums.BaseEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 枚举工具类:将枚举转为简单的kv
 * TODO 如何实现限制为实现某接口的枚举类
 *
 * @author aries
 * @date 2022/8/10
 */
public class EnumUtils {

  public static <T extends BaseEnum> List<Kv> list(Class<T> tClass) {
    return Arrays.stream(tClass.getEnumConstants()).map(EnumUtils::apply).collect(Collectors.toList());
  }

  @SafeVarargs
  public static <T extends BaseEnum> List<Kv> list(T... l) {
    return Arrays.stream(l).map(EnumUtils::apply).collect(Collectors.toList());
  }

  private static <T extends BaseEnum> Kv apply(T e) {
    return new Kv(e.getType(), e.getName());
  }
}
