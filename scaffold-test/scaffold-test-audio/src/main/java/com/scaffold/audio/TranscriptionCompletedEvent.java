package com.scaffold.audio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionCompletedEvent {
    private MeetingRecord record;
    private String callbackUrl;
}
