package com.scaffold.support.workorder;

import com.scaffold.support.conversation.SupportConversationService;
import com.scaffold.support.conversation.SupportMessageRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阶段二工单创建、查询、消息恢复和关闭接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examples/support/work-orders")
@Tag(name = "售后工单", description = "AI 分析并由 Java 创建、持久化和按当前用户查询售后工单")
public class WorkOrderController {

    private final WorkOrderService service;
    private final SupportConversationService conversationService;

    /**
     * 创建或按 requestId 返回当前用户已经创建的同一工单。
     */
    @PostMapping
    @Operation(summary = "创建售后工单",
            description = "先由 AI 提取工单意图，再由 Java 生成用户、会话、状态、ID 和创建时间并持久化；requestId 用于幂等")
    public WorkOrder create(@RequestBody @Valid CreateWorkOrderRequest request) {
        return service.create(request);
    }

    /**
     * 查询当前用户的有效工单列表。
     */
    @GetMapping
    @Operation(summary = "查询当前用户的工单", description = "按创建时间倒序返回当前登录用户有权查看的未删除工单")
    public List<WorkOrder> list() {
        return service.listCurrentUserWorkOrders();
    }

    /**
     * 查询当前用户可访问的指定工单。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询工单详情", description = "仅返回当前登录用户有权查看的指定工单")
    public WorkOrder get(@Parameter(description = "工单ID", required = true, example = "1") @PathVariable long id) {
        return service.getCurrentUserWorkOrder(id);
    }

    /**
     * 恢复指定工单最近的持久化对话消息。
     */
    @GetMapping("/{id}/messages")
    @Operation(summary = "查询工单对话历史", description = "返回当前用户工单最近 20 条持久化消息")
    public List<SupportMessageRes> messages(@PathVariable long id) {
        return conversationService.history(id);
    }

    /**
     * 关闭工单并同步清理对应的对话记忆。
     */
    @PostMapping("/{id}/close")
    @Operation(summary = "关闭工单", description = "关闭当前用户的工单并清理对应的多轮对话记忆")
    public WorkOrder close(@Parameter(description = "工单ID", required = true, example = "1") @PathVariable long id) {
        return service.close(id);
    }
}
