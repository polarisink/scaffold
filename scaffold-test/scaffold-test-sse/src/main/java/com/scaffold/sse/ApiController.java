package com.scaffold.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class ApiController {

    private final SseConnectionManager connectionManager;

    /** 实际项目中 userId 应从登录上下文获取，不应信任前端传入值。 */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String userId, @RequestParam(defaultValue = "") List<String> roomId) {
        return connectionManager.connect(userId, roomId);
    }

    /** 实际项目中 userId 应从登录上下文获取，此处作为测试页面参数保留。 */
    @DeleteMapping("/connections/{connectionId}")
    public DisconnectResult disconnect(@PathVariable String connectionId, @RequestParam String userId) {
        return new DisconnectResult(connectionManager.disconnect(userId, connectionId));
    }

    @PostMapping("/users/{userId}/messages")
    public SseSendResult sendToUser(@PathVariable String userId, @RequestBody PushMessage message) {
        return connectionManager.sendToUser(userId, message.event(), message.data());
    }

    @PostMapping("/rooms/{roomId}/messages")
    public SseSendResult sendToRoom(@PathVariable String roomId, @RequestBody PushMessage message) {
        return connectionManager.sendToRoom(roomId, message.event(), message.data());
    }

    @GetMapping("/stats")
    public OnlineStats stats() {
        return new OnlineStats(connectionManager.onlineUserCount(), connectionManager.onlineConnectionCount());
    }

    public record PushMessage(String event, Object data) {}
    public record OnlineStats(int users, int connections) {}
    public record DisconnectResult(boolean disconnected) {}
}
