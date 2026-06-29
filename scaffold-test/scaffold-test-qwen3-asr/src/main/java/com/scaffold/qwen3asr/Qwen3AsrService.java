package com.scaffold.qwen3asr;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

/** 负责请求校验、上传文件生命周期管理，并将推理委托给常驻 Worker。 */
@Service
public class Qwen3AsrService {

    private final Qwen3AsrProperties properties;
    private final Qwen3AsrWorker worker;

    public Qwen3AsrService(Qwen3AsrProperties properties, Qwen3AsrWorker worker) {
        this.properties = properties;
        this.worker = worker;
    }

    public Qwen3AsrResult transcribe(Qwen3AsrRequest request) {
        if (request.audioPath() == null || request.audioPath().isBlank()) {
            throw new Qwen3AsrException("audioPath 不能为空");
        }
        validateAudioPath(request.audioPath());
        validateTimeStamps(Boolean.TRUE.equals(request.returnTimeStamps()));
        return worker.transcribe(
                request.audioPath(),
                request.language(),
                Boolean.TRUE.equals(request.returnTimeStamps())
        );
    }

    public Qwen3AsrResult transcribe(MultipartFile audio, String language, boolean returnTimeStamps) {
        if (audio == null || audio.isEmpty()) {
            throw new Qwen3AsrException("上传音频不能为空");
        }
        validateTimeStamps(returnTimeStamps);

        try {
            // 使用随机文件名隔离并发请求，同时仅保留经过白名单校验的扩展名。
            Path uploadDir = Path.of(properties.uploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String originalFilename = audio.getOriginalFilename();
            String suffix = suffix(originalFilename);
            Path audioPath = uploadDir.resolve(UUID.randomUUID() + suffix);
            audio.transferTo(audioPath);
            try {
                return worker.transcribe(audioPath.toString(), language, returnTimeStamps);
            } finally {
                // 推理成功或失败都清理上传文件，避免大音频长期占用磁盘。
                Files.deleteIfExists(audioPath);
            }
        } catch (IOException e) {
            throw new Qwen3AsrException("保存上传音频失败", e);
        }
    }

    public boolean isWorkerRunning() {
        return worker.isRunning();
    }

    private void validateAudioPath(String audioPath) {
        if (!Files.isRegularFile(Path.of(audioPath))) {
            throw new Qwen3AsrException("音频文件不存在: " + audioPath);
        }
    }

    private void validateTimeStamps(boolean returnTimeStamps) {
        // 时间戳不是 ASR 主模型的直接输出，必须额外加载 Forced Aligner。
        if (returnTimeStamps && (properties.forcedAlignerPath() == null || properties.forcedAlignerPath().isBlank())) {
            throw new Qwen3AsrException("returnTimeStamps=true 时需要配置 qwen3-asr.forced-aligner-path");
        }
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
        // 不直接信任客户端文件名，异常扩展名统一回退为 wav。
        return value.matches("\\.[a-z0-9]{1,8}") ? value : ".wav";
    }
}
