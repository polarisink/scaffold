package jdk;

import cn.hutool.core.util.RandomUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * @author aries
 * @date 2022/11/1
 */
public class RefTest {

  Map<Integer, Integer> map = new HashMap<>();
  Map<Integer, Integer> map1 = new HashMap<>();

  @Test
  public void map() {
    Map<Integer, Integer> map2 = RandomUtil.randomBoolean() ? map : map1;
    map2.put(1, 1);
    System.out.println(map);
    System.out.println(map1);
    System.out.println(map2);
  }

  @Test
  public void get() {
    Map<Integer, List<Integer>> map = new HashMap<>();
    map.put(1, new ArrayList<>());
    List<Integer> integers = map.get(1);
    integers.add(23);
    System.out.println(map);
  }
}
