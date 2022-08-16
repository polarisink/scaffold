package polarisink.github.scaffold.bean.consts;

/**
 * 文件相关常量
 *
 * @author aries
 * @date 2022/5/5
 */
public class FileConst {

  public static final String MQTT_FILE_PATH = System.getProperty("user.home") + "/mqtt";

  public static final String FILE_PATH = System.getProperty("user.home") + "/files";

  private FileConst() {
  }
}
