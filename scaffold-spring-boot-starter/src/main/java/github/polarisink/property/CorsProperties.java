package github.polarisink.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS Properties
 *
 * @author Bill
 * @version 1.0
 * @since 2019-10-31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "scaffold.cors")
public class CorsProperties {
  private List<String> allowedOrigins;
}
