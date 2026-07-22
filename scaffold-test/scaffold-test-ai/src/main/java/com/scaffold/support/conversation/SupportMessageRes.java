package com.scaffold.support.conversation;

import java.time.Instant;

/**
 * 返回给前端的已持久化工单消息。
 */
public record SupportMessageRes(Long id, long sequence, String role, String content, Instant createdAt) {
}
