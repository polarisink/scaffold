package polarisink.github.scaffold.msg;

import org.springframework.core.env.Environment;
import polarisink.github.scaffold.utils.SpringContextUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @program: double-cache
 * @author: Hydra
 * @create: 2022-03-30 14:17
 **/
public class MessageSourceUtil {
  public static String getMsgSource() throws UnknownHostException {
    String host = InetAddress.getLocalHost().getHostAddress();
    Environment env = SpringContextUtil.getBean(Environment.class);
    String port = env.getProperty("server.port");
    return host + ":" + port;
  }
}
