package com.scaffold.audio;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TranscriptionEvent {
    private MultipartFile audioFile;
    private String title;
    private String callbackUrl;
    private String originalRequestId;
}
