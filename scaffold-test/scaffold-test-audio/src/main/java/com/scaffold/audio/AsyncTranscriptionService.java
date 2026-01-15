package com.scaffold.audio;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class AsyncTranscriptionService {
    @Autowired
    private MeetingRecordService meetingRecordService;

    @Async
    @EventListener
    public void handleTranscriptionRequest(TranscriptionEvent event) {
        try {
            MeetingRecord record = meetingRecordService.generateMeetingRecord(
                    event.getAudioFile(),
                    event.getTitle()
            );

            // 发送完成事件
            SpringUtil.getApplicationContext().publishEvent(
                    new TranscriptionCompletedEvent(record, event.getCallbackUrl())
            );

        } catch (Exception e) {
            log.error("异步转录失败", e);
            SpringUtil.getApplicationContext().publishEvent(
                    new TranscriptionFailedEvent(event.getOriginalRequestId(), e.getMessage())
            );
        }
    }


}
