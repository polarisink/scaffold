package com.github.polarisink.sse;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ApiController {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @GetMapping("/hello")
    public String hello(){
        return "hello world";
    }

    @PostMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamData(HttpServletRequest request) {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            try {
                // 模拟流式推送（发送5条消息，间隔1秒）
                for (int i = 1; i <= 5; i++) {
                    String data = String.format("ip: %s get data: %s", request.getRemoteHost(), LocalDateTime.now());
                    emitter.send(data);
                    Thread.sleep(1000); // 模拟延迟
                }
                emitter.complete();
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}