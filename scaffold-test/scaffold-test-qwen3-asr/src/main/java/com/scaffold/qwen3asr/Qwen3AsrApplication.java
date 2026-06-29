package com.scaffold.qwen3asr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Qwen3-ASR 离线语音识别服务入口。
 *
 * <p>配置属性通过 {@link ConfigurationPropertiesScan} 自动扫描并绑定到
 * {@link Qwen3AsrProperties}。</p>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Qwen3AsrApplication {

    public static void main(String[] args) {
        SpringApplication.run(Qwen3AsrApplication.class, args);
    }
}
