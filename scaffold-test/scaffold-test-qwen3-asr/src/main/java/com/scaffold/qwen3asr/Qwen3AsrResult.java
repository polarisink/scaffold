package com.scaffold.qwen3asr;

import java.util.List;

public record Qwen3AsrResult(
        String language,
        String text,
        List<TimeStamp> timeStamps,
        String audioPath,
        String modelPath
) {

    public record TimeStamp(String text, Double startTime, Double endTime) {
    }
}
