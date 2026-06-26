package com.scaffold.qwen3asr;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qwen3-asr")
public record Qwen3AsrProperties(
        String python,
        String script,
        String modelPath,
        String forcedAlignerPath,
        String deviceMap,
        String dtype,
        Integer maxInferenceBatchSize,
        Integer maxNewTokens,
        Long timeoutSeconds,
        String uploadDir
) {

    public Qwen3AsrProperties {
        if (python == null || python.isBlank()) {
            python = "python3";
        }
        if (script == null || script.isBlank()) {
            script = "scaffold-test/scaffold-test-qwen3-asr/scripts/qwen3_asr_cli.py";
        }
        if (deviceMap == null || deviceMap.isBlank()) {
            deviceMap = "auto";
        }
        if (dtype == null || dtype.isBlank()) {
            dtype = "auto";
        }
        if (maxInferenceBatchSize == null) {
            maxInferenceBatchSize = 8;
        }
        if (maxNewTokens == null) {
            maxNewTokens = 512;
        }
        if (timeoutSeconds == null) {
            timeoutSeconds = 900L;
        }
        if (uploadDir == null || uploadDir.isBlank()) {
            uploadDir = "target/qwen3-asr-uploads";
        }
    }
}
