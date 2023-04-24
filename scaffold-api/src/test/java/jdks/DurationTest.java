package jdks;

import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * @author aries
 * @date 2022/9/29
 */
public class DurationTest {

  @Test
  public void parse() {
    System.out.println(Duration.parse("P3D"));
  }

  @Test
  public void test() {
    //npe
    System.out.println(Duration.between(null, LocalDateTime.now()));
  }
}
