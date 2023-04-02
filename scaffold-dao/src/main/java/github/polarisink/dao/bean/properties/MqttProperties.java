package github.polarisink.dao.bean.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lqs
 * @date 2023/4/2
 */
@Data
@Configuration
@ConfigurationProperties("mqtt")
public class MqttProperties {
  private String url;
  private String username;
  private String password;
  private String produceClientId;
  private String consumeClientId;
  private Integer completionTimeout=3000;
  private String defaultTopic;

}
