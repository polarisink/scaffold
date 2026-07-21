package com.scaffold.support.conversation;

import java.time.Instant;

public record SupportMessage(Long id, long sequence, String role, String content, Instant createdAt) {
}
