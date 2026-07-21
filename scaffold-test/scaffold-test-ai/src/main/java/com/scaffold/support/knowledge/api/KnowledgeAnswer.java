package com.scaffold.support.knowledge.api;

import java.util.List;

public record KnowledgeAnswer(String answer, List<KnowledgeSource> sources, boolean grounded) {
}
