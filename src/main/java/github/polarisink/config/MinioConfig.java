package github.polarisink.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;

/**
 * @author lqs
 * @date 2022/3/18
 */
//@Getter
//@Configuration
//@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    private String endpoint;
    private Integer port;
    private String accessKey;
    private String secretKey;
    private Boolean secure;
    private String bucketName;

    @Bean
    public MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(endpoint, port, secure)
                .credentials(accessKey, secretKey)
                .build();

    }

}
