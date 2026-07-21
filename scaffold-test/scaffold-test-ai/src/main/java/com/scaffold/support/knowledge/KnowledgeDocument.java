package com.scaffold.support.knowledge;

import java.time.Instant;

public record KnowledgeDocument(String documentId, String title, String version,
                                Instant updatedAt, String content) {
}
