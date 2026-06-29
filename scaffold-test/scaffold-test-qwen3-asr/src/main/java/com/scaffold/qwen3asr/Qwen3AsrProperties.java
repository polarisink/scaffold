package com.scaffold.qwen3asr;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@code qwen3-asr} 配置项。
 *
 * @param python Python 解释器路径
 * @param script Python 推理脚本路径
 * @param modelPath 本地 Qwen3-ASR 模型目录
 * @param forcedAlignerPath 可选的本地 Forced Aligner 模型目录
 * @param deviceMap 推理设备，例如 {@code auto}、{@code cuda:0}、{@code mps} 或 {@code cpu}
 * @param dtype 模型计算精度
 * @param maxInferenceBatchSize 最大推理批次大小
 * @param maxNewTokens 单次推理最大生成 token 数
 * @param timeoutSeconds 模型启动及单次推理的超时时间
 * @param uploadDir 上传音频的临时存放目录
 */
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

    private static final String MODULE_DIR = "scaffold-test/scaffold-test-qwen3-asr";

    /** 为未配置的可选项填充安全默认值，必填的模型路径留给启动 Worker 时校验。 */
    public Qwen3AsrProperties {
        if (python == null || python.isBlank()) {
            python = firstExistingFile(
                    ".venv/bin/python",
                    MODULE_DIR + "/.venv/bin/python",
                    ".venv/Scripts/python.exe",
                    MODULE_DIR + "/.venv/Scripts/python.exe"
            );
            if (python == null) {
                python = "python3";
            }
        }
        if (script == null || script.isBlank()) {
            script = firstExistingFile(
                    "scripts/qwen3_asr_cli.py",
                    MODULE_DIR + "/scripts/qwen3_asr_cli.py"
            );
        }
        if (modelPath == null || modelPath.isBlank()) {
            modelPath = firstExistingDirectory(
                    "models/Qwen3-ASR-0.6B",
                    MODULE_DIR + "/models/Qwen3-ASR-0.6B"
            );
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

    private static String firstExistingFile(String... candidates) {
        return firstExistingPath(false, candidates);
    }

    private static String firstExistingDirectory(String... candidates) {
        return firstExistingPath(true, candidates);
    }

    private static String firstExistingPath(boolean directory, String... candidates) {
        for (String candidate : candidates) {
            Path path = Path.of(candidate).toAbsolutePath().normalize();
            if (directory ? Files.isDirectory(path) : Files.isRegularFile(path)) {
                return path.toString();
            }
        }
        return null;
    }
}
