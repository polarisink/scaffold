package github.polarisink.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lqs
 * @describe
 * @date 2021/11/21
 */
@Data
@Component
@ConfigurationProperties(prefix = "mq")
public class MqProperties {
	private String defaultExchange;
	private String routeKey;
	private String queue;
}
