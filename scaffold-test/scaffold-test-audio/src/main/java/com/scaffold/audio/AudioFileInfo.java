package com.scaffold.audio;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioFileInfo {
    private Double duration;
    private Integer sampleRate;
    private Integer channels;
    private String codec;
}
