package jdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

/**
 * @author aries
 * @date 2022/9/22
 */
public class MapTest {

  @Test
  public void test() throws JsonProcessingException {
    //System.out.println(new ObjectMapper().writeValueAsString(Map.of("a", "b")));
    String s = "xidjosjciovvss";
    System.out.println(s);
    System.out.println(s.substring(1, s.length() - 1));
  }
}
