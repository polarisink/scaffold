package com.scaffold.support.knowledge;

import com.scaffold.ai.chat.AiChatService;
import com.scaffold.support.knowledge.api.KnowledgeAnswer;
import com.scaffold.support.knowledge.api.KnowledgeRequest;
import com.scaffold.support.knowledge.api.KnowledgeSource;
import com.scaffold.support.knowledge.retrieval.KnowledgeRetriever;
import com.scaffold.support.identity.SupportCurrentUserProvider;
import com.scaffold.support.workorder.OrderNotAccessibleException;
import com.scaffold.support.workorder.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 基于检索结果生成有依据的售后回答；无可靠片段时直接拒答，不调用大模型猜测。
 */
@Service
@Lazy
@RequiredArgsConstructor
public class KnowledgeService {

    private static final String REFUSAL = "当前知识库中没有足够依据回答该问题，请转人工处理。";
    private final KnowledgeRetriever retriever;
    private final AiChatService aiChatService;
    private final WorkOrderRepository workOrders;
    private final SupportCurrentUserProvider currentUser;

    /** 检索与问题相关的知识片段并生成带来源的回答。 */
    public KnowledgeAnswer answer(KnowledgeRequest request) {
        long userId = currentUser.requireUserId();
        if (workOrders.findByIdAndUserIdAndDeleted(request.workOrderId(), userId, 0).isEmpty()) {
            throw new OrderNotAccessibleException();
        }
        String question = request.question().trim();
        List<KnowledgeDocument> documents = retriever.search(question, 3);
        if (documents.isEmpty()) {
            return new KnowledgeAnswer(REFUSAL, List.of(), false);
        }
        String evidence = documents.stream().map(document -> "[" + document.documentId() + "] " + document.content())
                .collect(java.util.stream.Collectors.joining("\n"));
        String system = "你是售后知识助手。只能依据以下资料回答；资料不足时必须明确拒答。回答中不要虚构来源。\n" + evidence;
        String answer = aiChatService.chat("knowledge:" + UUID.randomUUID(), system, question,
                Map.of(), new String[0]);
        List<KnowledgeSource> sources = documents.stream().map(document -> new KnowledgeSource(
                document.documentId(), document.title(), document.version(), document.updatedAt())).distinct().toList();
        return new KnowledgeAnswer(answer, sources, true);
    }
}
