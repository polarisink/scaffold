package com.scaffold.qwen3asr;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 一次语音转写的结果及实际推理环境。
 *
 * @param language   模型识别出的语言
 * @param text       完整转写文本
 * @param timeStamps 可选的分段时间戳
 * @param audioPath  本次推理使用的音频路径
 * @param modelPath  本次推理使用的模型目录
 * @param deviceMap  实际使用的推理设备
 * @param dtype      实际使用的计算精度
 */
@Schema(description = "语音转写结果")
public record Qwen3AsrResult(
        @Schema(description = "模型识别出的语言", example = "Chinese") String language,
        @Schema(description = "完整转写文本") String text,
        @Schema(description = "可选的分段时间戳") List<TimeStamp> timeStamps,
        @Schema(description = "本次推理使用的音频路径") String audioPath,
        @Schema(description = "本次推理使用的模型目录") String modelPath,
        @Schema(description = "实际使用的推理设备", example = "cpu") String deviceMap,
        @Schema(description = "实际使用的计算精度", example = "float32") String dtype
) {

    /**
     * 单段文本在音频中的起止时间，单位为秒。
     */
    @Schema(description = "单段文本时间戳")
    public record TimeStamp(
            @Schema(description = "分段文本") String text,
            @Schema(description = "开始时间，单位为秒") Double startTime,
            @Schema(description = "结束时间，单位为秒") Double endTime
    ) {
    }
}
