package github.polarisink.cache.msg;

import github.polarisink.common.utils.SpringContextUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.core.env.Environment;

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
