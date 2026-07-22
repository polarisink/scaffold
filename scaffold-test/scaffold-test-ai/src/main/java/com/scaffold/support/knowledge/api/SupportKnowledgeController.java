package com.scaffold.support.knowledge.api;

import com.scaffold.support.knowledge.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 阶段五基于 pgvector 检索结果生成有依据回答的接口。
 */
@RestController
@Lazy
@RequestMapping("/api/examples/support/knowledge")
@RequiredArgsConstructor
@Tag(name = "售后知识库", description = "基于持久化售后制度和产品说明生成有依据的回答")
public class SupportKnowledgeController {
    private final KnowledgeService service;

    @PostMapping("/answer")
    @Operation(summary = "检索并回答售后知识问题")
    /** 校验请求后执行知识检索和有依据回答。 */
    public KnowledgeAnswer answer(@RequestBody @Valid KnowledgeRequest request) {
        return service.answer(request);
    }
}
