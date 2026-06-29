package com.scaffold.qwen3asr;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 服务端本地音频转写请求。
 *
 * @param audioPath 服务进程可访问的音频文件路径
 * @param language 可选的语种提示，例如 {@code Chinese} 或 {@code English}
 * @param returnTimeStamps 是否使用 Forced Aligner 返回分段时间戳
 */
@Schema(description = "服务端本地音频转写请求")
public record Qwen3AsrRequest(
        @Schema(description = "服务进程可访问的音频文件路径，支持 WAV、MP3、FLAC、OGG、M4A/AAC、WebM/Opus",
                example = "/data/audio/example.wav", requiredMode = Schema.RequiredMode.REQUIRED)
        String audioPath,
        @Schema(description = "可选语种提示", example = "Chinese")
        String language,
        @Schema(description = "是否返回分段时间戳", defaultValue = "false")
        Boolean returnTimeStamps
) {
}
