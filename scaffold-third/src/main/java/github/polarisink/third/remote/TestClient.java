package github.polarisink.third.remote;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author aries
 * @date 2022/12/28
 */
@HttpExchange("/test")
public interface TestClient {

  /**
   * 远程ping接口
   * @return
   */
  @GetExchange("/ping")
  String ping();

}
