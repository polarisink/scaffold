package com.scaffold.support.suggestion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 阶段六综合处理建议接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examples/support/work-orders/{workOrderId}/suggestions")
@Tag(name = "售后综合处理建议", description = "基于工单、只读订单工具和知识库生成可追溯的处理建议")
public class HandlingSuggestionController {

    private final HandlingSuggestionService service;

    @PostMapping
    @Operation(summary = "生成综合处理建议", description = "只生成并保存建议，不修改工单、订单或退款状态")
    public HandlingSuggestion generate(
            @Parameter(description = "工单ID", required = true) @PathVariable long workOrderId) {
        return service.generate(workOrderId);
    }

    @GetMapping("/latest")
    @Operation(summary = "查询最近一次综合处理建议")
    public HandlingSuggestion latest(
            @Parameter(description = "工单ID", required = true) @PathVariable long workOrderId) {
        return service.latest(workOrderId);
    }
}
