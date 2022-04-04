package github.polarisink.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lqs
 * @describe
 * @date 2022/4/3
 */

@Configuration
@ConfigurationProperties(prefix = "scaffold.i18n")
public class LangProperties {

}
