package com.scaffold.support.conversation;

import java.time.Instant;

public record SupportMessageRes(Long id, long sequence, String role, String content, Instant createdAt) {
}
