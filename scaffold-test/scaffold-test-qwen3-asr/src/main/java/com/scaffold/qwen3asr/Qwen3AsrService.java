package com.scaffold.qwen3asr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class Qwen3AsrService {

    private final Qwen3AsrProperties properties;
    private final ObjectMapper objectMapper;

    public Qwen3AsrService(Qwen3AsrProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public Qwen3AsrResult transcribe(Qwen3AsrRequest request) {
        if (request.audioPath() == null || request.audioPath().isBlank()) {
            throw new Qwen3AsrException("audioPath 不能为空");
        }
        return runCli(
                request.audioPath(),
                request.language(),
                Boolean.TRUE.equals(request.returnTimeStamps()),
                choose(request.modelPath(), properties.modelPath()),
                choose(request.forcedAlignerPath(), properties.forcedAlignerPath())
        );
    }

    public Qwen3AsrResult transcribe(MultipartFile audio, String language, boolean returnTimeStamps) {
        if (audio == null || audio.isEmpty()) {
            throw new Qwen3AsrException("上传音频不能为空");
        }

        try {
            Path uploadDir = Path.of(properties.uploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String originalFilename = audio.getOriginalFilename();
            String suffix = suffix(originalFilename);
            Path audioPath = uploadDir.resolve(UUID.randomUUID() + suffix);
            audio.transferTo(audioPath);
            return runCli(
                    audioPath.toString(),
                    language,
                    returnTimeStamps,
                    properties.modelPath(),
                    properties.forcedAlignerPath()
            );
        } catch (IOException e) {
            throw new Qwen3AsrException("保存上传音频失败", e);
        }
    }

    private Qwen3AsrResult runCli(
            String audioPath,
            String language,
            boolean returnTimeStamps,
            String modelPath,
            String forcedAlignerPath
    ) {
        if (modelPath == null || modelPath.isBlank()) {
            throw new Qwen3AsrException("请配置 qwen3-asr.model-path 为本地 Qwen3-ASR 模型目录");
        }
        if (!Files.isDirectory(Path.of(modelPath))) {
            throw new Qwen3AsrException("模型目录不存在: " + modelPath);
        }
        if (returnTimeStamps && (forcedAlignerPath == null || forcedAlignerPath.isBlank())) {
            throw new Qwen3AsrException("returnTimeStamps=true 时需要配置 qwen3-asr.forced-aligner-path");
        }

        List<String> command = new ArrayList<>();
        command.add(properties.python());
        command.add(properties.script());
        command.add("--model-path");
        command.add(modelPath);
        command.add("--audio");
        command.add(audioPath);
        command.add("--device-map");
        command.add(properties.deviceMap());
        command.add("--dtype");
        command.add(properties.dtype());
        command.add("--max-inference-batch-size");
        command.add(String.valueOf(properties.maxInferenceBatchSize()));
        command.add("--max-new-tokens");
        command.add(String.valueOf(properties.maxNewTokens()));
        if (language != null && !language.isBlank()) {
            command.add("--language");
            command.add(language);
        }
        if (returnTimeStamps) {
            command.add("--return-time-stamps");
        }
        if (forcedAlignerPath != null && !forcedAlignerPath.isBlank()) {
            command.add("--forced-aligner-path");
            command.add(forcedAlignerPath);
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.environment().putAll(offlineEnvironment());
        builder.redirectErrorStream(true);

        try {
            Process process = builder.start();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Thread reader = new Thread(() -> copy(process.getInputStream(), output), "qwen3-asr-output");
            reader.start();

            boolean finished = process.waitFor(properties.timeoutSeconds(), TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new Qwen3AsrException("Qwen3-ASR 本地推理超时: " + Duration.ofSeconds(properties.timeoutSeconds()));
            }
            reader.join(TimeUnit.SECONDS.toMillis(5));

            String text = output.toString(StandardCharsets.UTF_8);
            if (process.exitValue() != 0) {
                throw new Qwen3AsrException("Qwen3-ASR 本地推理失败: " + text);
            }
            return objectMapper.readValue(text, Qwen3AsrResult.class);
        } catch (IOException e) {
            throw new Qwen3AsrException("启动 Qwen3-ASR 本地推理进程失败", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Qwen3AsrException("Qwen3-ASR 本地推理被中断", e);
        }
    }

    private static Map<String, String> offlineEnvironment() {
        return Map.of(
                "HF_HUB_OFFLINE", "1",
                "TRANSFORMERS_OFFLINE", "1",
                "HF_DATASETS_OFFLINE", "1",
                "PYTHONIOENCODING", "UTF-8"
        );
    }

    private static void copy(InputStream inputStream, ByteArrayOutputStream output) {
        try (inputStream) {
            inputStream.transferTo(output);
        } catch (IOException ignored) {
        }
    }

    private static String choose(String first, String second) {
        return first == null || first.isBlank() ? second : first;
    }

    private static String suffix(String filename) {
        if (filename == null || filename.isBlank()) {
            return ".wav";
        }
        int index = filename.lastIndexOf('.');
        if (index < 0 || index == filename.length() - 1) {
            return ".wav";
        }
        String value = filename.substring(index).toLowerCase(Locale.ROOT);
        return value.matches("\\.[a-z0-9]{1,8}") ? value : ".wav";
    }
}
