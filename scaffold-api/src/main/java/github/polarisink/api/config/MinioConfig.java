package github.polarisink.api.config;

import github.polarisink.dao.bean.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author aries
 * @date 2022/8/17
 */
@Configuration
@RequiredArgsConstructor
public class MinioConfig {
  private final MinioProperties prop;

  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder().endpoint(prop.getEndpoint()).credentials(prop.getAccessKey(), prop.getSecretKey()).build();
  }
}
