package github.polarisink.scaffold.infrastructure.util;


import static java.util.stream.Collectors.toList;

import github.polarisink.scaffold.domain.KeyValue;
import github.polarisink.scaffold.infrastructure.asserts.BaseEnum;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;

/**
 * 枚举工具类:将枚举转为简单的kv
 * @author aries
 * @date 2022/8/10
 */
public class EnumUtils {

  public static <T extends BaseEnum> List<KeyValue> list(Class<T> tClass) {
    return Arrays.stream(tClass.getEnumConstants()).map(EnumUtils::apply).collect(toList());
  }

  @SafeVarargs
  public static <T extends BaseEnum> List<KeyValue> list(T... l) {
    return Arrays.stream(l).map(EnumUtils::apply).collect(toList());
  }

  private static <T extends BaseEnum> KeyValue apply(T e) {
    return new KeyValue(e.getCode(), e.getMessage());
  }

  public static <T extends BaseEnum> BaseEnum getByCode(Class<T> tClass,int code){

      return null;
  }

    public static void main(String[] args) {
        Reflections reflections = new Reflections("github.polarisink.scaffold");
        Set<Class<? extends BaseEnum>> monitorClasses = reflections.getSubTypesOf(BaseEnum.class);
        System.out.println("size:"+monitorClasses.size());
        monitorClasses.forEach(m->{
            try {
                System.out.println("name:"+m.getSimpleName());
                BaseEnum[] enumConstants = m.getEnumConstants();

                for (BaseEnum anEnum : enumConstants) {
                    System.out.println("enumName:"+anEnum+"  value:"+anEnum.getCode()+"  name:"+anEnum.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}
