package jdk;

import org.junit.Test;

import java.time.Duration;

/**
 * @author aries
 * @date 2022/9/29
 */
public class DurationTest {
  @Test
  public void parse() {
    System.out.println(Duration.parse("P3D"));
  }
}
