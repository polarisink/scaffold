package com.lqs.scaffold.config;

import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lqs
 * @date 2022/3/18
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "minio")
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
