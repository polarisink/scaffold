package com.scaffold.support.intent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 阶段一工单意图分析接口，不负责创建或修改业务工单。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examples/support/intents")
@Tag(name = "售后工单意图", description = "使用大模型把自然语言售后描述转换为结构化工单意图")
public class SupportController {

    private final SupportIntentService service;

    @PostMapping("/analyze")
    @Operation(summary = "分析工单意图",
            description = "加载 support-intent@v1 Prompt，将售后描述转换并校验为 WorkOrderIntent；不会创建工单")
    /** 将自然语言售后描述转换为受校验的结构化意图。 */
    public WorkOrderIntent analyze(@RequestBody @Valid AnalyzeRequest request) {
        return service.analyze(request);
    }
}
