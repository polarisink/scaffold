package com.scaffold.audio;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CachedTranscriptionService {

    private final WhisperTranscriptionService transcriptionService;

    @Cacheable(value = "transcriptions", key = "#audioFilePath + '_' + #language")
    public TranscriptionResult getTranscription(String audioFilePath, String language) {
        return transcriptionService.transcribeAudio(audioFilePath, language);
    }

}