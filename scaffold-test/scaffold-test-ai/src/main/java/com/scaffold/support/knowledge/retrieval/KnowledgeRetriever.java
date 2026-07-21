package com.scaffold.support.knowledge.retrieval;

import com.scaffold.support.knowledge.KnowledgeDocument;

import java.util.List;

/**
 * 知识检索接口，用于隔离问答编排与具体向量数据库实现。
 */
public interface KnowledgeRetriever {
    List<KnowledgeDocument> search(String query, int limit);
}
