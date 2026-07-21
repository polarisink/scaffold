package com.scaffold.support.knowledge.api;

import java.time.Instant;

public record KnowledgeSource(String documentId, String title, String version, Instant updatedAt) {
}
