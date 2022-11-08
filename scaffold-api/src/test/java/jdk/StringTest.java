package jdk;

import cn.hutool.core.util.StrUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author aries
 * @date 2022/10/10
 */
public class StringTest {
  @Test
  public void testSplit() {
    String name = "ciufhiadoijf";
    System.out.println(StrUtil.split(name, "("));
  }

  @Test
  public  void  testJoin(){
    List<String> list = Arrays.asList("1","2","3");
    System.out.println(String.join(",",list));
  }
}
