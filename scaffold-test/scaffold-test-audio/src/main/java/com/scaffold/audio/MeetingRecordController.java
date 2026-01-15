package com.scaffold.audio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MeetingRecordController {
    @Autowired
    private MeetingRecordService meetingRecordService;

    /**
     * 上传音频文件生成会议记录
     */
    @PostMapping("/generate")
    public ResponseEntity<MeetingRecord> generateRecord(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam(value = "title", required = false) String title) {

        try {
            if (audioFile.isEmpty()) {
                return null;
            }

            // 验证文件类型
            String contentType = audioFile.getContentType();
            if (!isValidAudioFormat(contentType)) {
                return null;
            }

            MeetingRecord record = meetingRecordService.generateMeetingRecord(
                    audioFile,
                    title != null ? title : "会议记录_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            return null;

        } catch (Exception e) {
            log.error("生成会议记录失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * 批量处理会议录音
     */
    @PostMapping("/batch-generate")
    public ResponseEntity<List<MeetingRecord>> batchGenerateRecords(
            @RequestParam("audioFiles") MultipartFile[] audioFiles) {

        List<MeetingRecord> records = new ArrayList<>();

        for (MultipartFile file : audioFiles) {
            try {
                MeetingRecord record = meetingRecordService.generateMeetingRecord(
                        file,
                        "批量处理_" + file.getOriginalFilename()
                );
                records.add(record);
            } catch (Exception e) {
                log.error("处理音频文件失败: {}", file.getOriginalFilename(), e);
            }
        }

        return ResponseEntity.ok(records);
    }

    /**
     * 获取会议记录详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingRecord> getRecord(@PathVariable Long id) {
        // 实现获取记录逻辑
        return ResponseEntity.ok(null); // 简化实现
    }

    private boolean isValidAudioFormat(String contentType) {
        return contentType != null && (
                contentType.startsWith("audio/") ||
                        contentType.equals("video/mp4") ||  // MP4也包含音频
                        contentType.equals("video/x-msvideo") // AVI也包含音频
        );
    }
}
