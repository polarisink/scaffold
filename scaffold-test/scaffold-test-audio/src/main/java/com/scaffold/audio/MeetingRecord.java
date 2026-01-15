package com.scaffold.audio;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeetingRecord {
    private String title;
    private String originalAudioPath;
    private String processedAudioPath;
    private String rawTranscript;
    private String processedTranscript;
    private LocalDateTime createdAt;
}
