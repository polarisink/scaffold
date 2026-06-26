package com.scaffold.qwen3asr;

public record Qwen3AsrRequest(
        String audioPath,
        String language,
        Boolean returnTimeStamps,
        String modelPath,
        String forcedAlignerPath
) {
}
