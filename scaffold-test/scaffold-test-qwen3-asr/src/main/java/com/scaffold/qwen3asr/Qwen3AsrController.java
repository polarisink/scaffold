package com.scaffold.qwen3asr;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 对外提供 Qwen3-ASR 转写与运行状态接口。
 *
 * <p>既支持服务端可访问的本地文件路径，也支持 multipart 音频上传。</p>
 * <p>当前已验证的音频格式包括 WAV、MP3、FLAC、OGG、M4A/AAC 和 WebM/Opus。</p>
 */
@RestController
@RequestMapping("/qwen3-asr")
@Tag(name = "Qwen3-ASR", description = "本地音频转写接口")
public class Qwen3AsrController {

    private final Qwen3AsrService qwen3AsrService;

    public Qwen3AsrController(Qwen3AsrService qwen3AsrService) {
        this.qwen3AsrService = qwen3AsrService;
    }

    /** 使用服务端本地文件路径发起转写。 */
    @PostMapping("/transcribe-path")
    @Operation(summary = "转写本地音频", description = "转写服务端可访问的本地音频文件")
    public Qwen3AsrResult transcribePath(@ParameterObject @ModelAttribute Qwen3AsrRequest request) {
        return qwen3AsrService.transcribe(request);
    }

    /** 上传音频并转写；上传产生的临时文件会在推理结束后删除。 */
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传音频并转写", description = "上传音频文件并返回语音识别文本")
    public Qwen3AsrResult transcribe(
            @Parameter(description = "待转写的音频文件，支持 WAV、MP3、FLAC、OGG、M4A/AAC、WebM/Opus",
                    required = true)
            @RequestPart("audio") MultipartFile audio,
            @Parameter(description = "可选语种提示，例如 Chinese 或 English")
            @RequestParam(name = "language", required = false) String language,
            @Parameter(description = "是否返回分段时间戳；启用时需要配置 Forced Aligner")
            @RequestParam(name = "returnTimeStamps", defaultValue = "false") boolean returnTimeStamps
    ) {
        return qwen3AsrService.transcribe(audio, language, returnTimeStamps);
    }

    /**
     * 返回 Web 服务和模型进程状态。
     *
     * <p>模型进程采用懒加载，因此首次转写前 {@code modelStatus} 为 {@code NOT_LOADED}
     * 属于正常状态。</p>
     */
    @GetMapping("/health")
    @Operation(summary = "服务健康检查", description = "查看 Web 服务和模型进程状态")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "modelStatus", qwen3AsrService.isWorkerRunning() ? "READY" : "NOT_LOADED"
        );
    }

    /** 将可预期的参数或本地推理错误统一转换为 400 响应。 */
    @ExceptionHandler(Qwen3AsrException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleQwen3AsrException(Qwen3AsrException exception) {
        return Map.of("message", exception.getMessage());
    }
}
