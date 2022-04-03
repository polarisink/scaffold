package github.polarisink.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS Properties
 *
 * @author Bill
 * @version 1.0
 * @since 2019-10-31
 */
@ConfigurationProperties(prefix = "scaffold.cors")
@Component
@Data
public class CorsProperties {
  private List<String> allowedOrigins;
}
