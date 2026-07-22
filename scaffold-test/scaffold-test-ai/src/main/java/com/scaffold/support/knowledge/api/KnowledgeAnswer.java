package com.scaffold.support.knowledge.api;

import java.util.List;

/**
 * 知识库回答、引用来源及其是否有可靠依据的标志。
 */
public record KnowledgeAnswer(String answer, List<KnowledgeSource> sources, boolean grounded) {
}
