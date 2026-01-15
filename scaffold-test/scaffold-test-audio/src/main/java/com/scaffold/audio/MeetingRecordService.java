package com.scaffold.audio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingRecordService {
    private final AudioPreprocessingService preprocessingService;

    private final WhisperTranscriptionService transcriptionService;

    /**
     * 生成会议记录
     */
    public MeetingRecord generateMeetingRecord(MultipartFile audioFile, String meetingTitle) {
        try {
            // 1. 保存上传的音频文件
            String originalFilePath = saveUploadedFile(audioFile);

            // 2. 预处理音频
            String processedFilePath = preprocessingService.preprocessAudio(originalFilePath);

            // 3. 语音识别
            TranscriptionResult result = transcriptionService.transcribeAudio(processedFilePath, "zh");

            if (!result.isSuccess()) {
                throw new RuntimeException("语音识别失败: " + result.getErrorMessage());
            }

            // 4. 生成会议记录
            MeetingRecord record = new MeetingRecord();
            record.setTitle(meetingTitle);
            record.setOriginalAudioPath(originalFilePath);
            record.setProcessedAudioPath(processedFilePath);
            record.setRawTranscript(result.getText());
            record.setProcessedTranscript(postProcessTranscript(result.getText()));
            record.setCreatedAt(LocalDateTime.now());

            // 5. 清理临时文件
            cleanupTempFiles(processedFilePath);

            return record;

        } catch (Exception e) {
            log.error("生成会议记录失败", e);
            throw new RuntimeException("会议记录生成失败: " + e.getMessage());
        }
    }

    /**
     * 后处理识别结果
     */
    private String postProcessTranscript(String rawTranscript) {
        // 移除时间戳
        String processed = rawTranscript.replaceAll("\\[\\d{2}:\\d{2}.\\d{3} --> \\d{2}:\\d{2}.\\d{3}\\]", "");

        // 清理多余的空白字符
        processed = processed.replaceAll("\\s+", " ").trim();

        // 按句子分割，便于后续处理
        String[] sentences = processed.split("[。！？.!?]");

        StringBuilder formatted = new StringBuilder();
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (!sentence.isEmpty()) {
                formatted.append(sentence).append("。\n");
            }
        }

        return formatted.toString();
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        String fileName = "audio_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = System.getProperty("java.io.tmpdir") + "/" + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//            file.transferTo(fos);
        }

        return filePath;
    }

    private void cleanupTempFiles(String... filePaths) {
        for (String filePath : filePaths) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                log.warn("删除临时文件失败: {}", filePath, e);
            }
        }
    }
}
