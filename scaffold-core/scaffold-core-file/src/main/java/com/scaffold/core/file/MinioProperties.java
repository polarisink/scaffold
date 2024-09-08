package com.scaffold.core.file;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * minio基本配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
class MinioProperties implements InitializingBean {

    /**
     * 地址
     */
    private String endpoint = "http://172.16.1.86:9000";
    /**
     * 账户
     */
    private String accessKey = "admin";
    /**
     * 密码
     */
    private String secretKey = "adminpassword";

    /**
     * bucket
     */
    private String bucket = "train";

    /**
     * 过期时间单位
     */
    private TimeUnit timeUnit = TimeUnit.DAYS;
    /**
     * 过期时间
     */
    private int timeout = 7;
    /**
     * 对象名前缀
     */
    private String objectNamePrefix = "uploads";

    /**
     * 文件最小限制
     */
    private int lowerLimit = 1;
    /**
     * 文件最大限制，注意和servlet文件大小限制搭配使用
     * 25 << 22 = 1024 * 1024 * 100 = 100MB
     */
    private int upperLimit = 25 << 22;


    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //过期时间
        boolean timeoutFlag = timeout > 0;
        //对象大小限制
        boolean limitFlag = lowerLimit > 0 && upperLimit > lowerLimit;
        //校验地址
        boolean endpointFlag = endpoint.startsWith("http://") || endpoint.startsWith("https://");
        Assert.state(timeoutFlag && limitFlag && endpointFlag, "illegal minio properties");
    }

}