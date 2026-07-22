package com.scaffold.nativetest;

import com.scaffold.sse.SseConnectionManager;
import com.scaffold.sse.SseSendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/** SSE endpoints used by the native test console. */
@RestController
@RequestMapping("/api/native-test/sse")
@RequiredArgsConstructor
public class SseNativeTestController {

    private final SseConnectionManager connectionManager;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String userId,
                              @RequestParam(defaultValue = "") List<String> roomId) {
        return connectionManager.connect(userId, roomId);
    }

    @PostMapping("/users/{userId}")
    public SseSendResult sendToUser(@PathVariable String userId, @RequestBody TestMessage message) {
        return connectionManager.sendToUser(userId, message.event(), message.data());
    }

    @PostMapping("/rooms/{roomId}")
    public SseSendResult sendToRoom(@PathVariable String roomId, @RequestBody TestMessage message) {
        return connectionManager.sendToRoom(roomId, message.event(), message.data());
    }

    @GetMapping("/stats")
    public OnlineStats stats() {
        return new OnlineStats(connectionManager.onlineUserCount(), connectionManager.onlineConnectionCount());
    }

    public record TestMessage(String event, Object data) {}

    public record OnlineStats(int users, int connections) {}
}
