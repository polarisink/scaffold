package github.polarisink.dao.utils;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * TdEngine 工具类
 *
 * @author lqs
 * @date 2023/1/13
 */
public class TdUtil {

  public static <T> List<T> parse2list(List<Map<String, Object>> mapList, Class<T> tClass) {
    return mapList.stream().map(map -> parse2obj(map, tClass)).collect(Collectors.toList());
  }


  /**
   * map转化为tdEngine实体类
   *
   * @param map
   * @param tClass
   * @param <T>
   * @return
   */
  @SneakyThrows
  public static <T> T parse2obj(Map<String, Object> map, Class<T> tClass) {
    T t = tClass.getDeclaredConstructor().newInstance();
    for (Field field : tClass.getDeclaredFields()) {
      field.setAccessible(true);
      JsonAlias jsonAlias = field.getDeclaredAnnotation(JsonAlias.class);
      String key = field.getName();
      if (Objects.nonNull(jsonAlias) && jsonAlias.value().length > 0) {
        key = jsonAlias.value()[0];
      }
      Object o = map.get(key);
      if (Objects.isNull(o)) {
        continue;
      }
      //如果是字符串类型,就转为byte数组,再转为string
      if (field.getType().equals(String.class)) {
        o = new String((byte[]) o);
      }
      field.set(t, o);
    }
    return t;
  }

  @SneakyThrows
  public static void main(String[] args) {
    //Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader objects from one decode to the next.
    Map<String, Object> map = Map.of("name", "lqs".getBytes(),"age",11);
    System.out.println(map);
    System.out.println(parse2obj(map, Man.class));
  }

  @Data
  static class Man {

    private String name;
    @JsonAlias("age")
    private Integer a;
  }
}
