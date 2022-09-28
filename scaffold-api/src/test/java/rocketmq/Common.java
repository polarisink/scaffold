package rocketmq;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author aries
 * @date 2022/9/28
 */
public class Common {
  private static String s = "E:\\ideaProjects\\scaffold\\.env";
  public static final String ROCKETMQ_URL;

  static {
    //Resource resource = new FileUrlResource(s);
    Properties props = new Properties();
    try (InputStream in = new FileInputStream(s)) {
      props.load(in);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 通过key获取value
    // 使用了env进行配置,现在不知道如何解决
    ROCKETMQ_URL = props.getProperty("ROCKETMQ_URL");
  }
}
