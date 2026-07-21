package com.scaffold.support.workorder;

import com.scaffold.support.conversation.SupportConversationService;
import com.scaffold.support.conversation.SupportMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/examples/support/work-orders")
@Tag(name = "售后工单", description = "AI 分析并由 Java 创建、持久化和按当前用户查询售后工单")
public class WorkOrderController {

    private final WorkOrderService service;
    private final SupportConversationService conversationService;

    @PostMapping
    @Operation(summary = "创建售后工单",
            description = "先由 AI 提取工单意图，再由 Java 生成用户、会话、状态、ID 和创建时间并持久化；requestId 用于幂等")
    public WorkOrder create(@RequestBody @Valid CreateWorkOrderRequest request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "查询当前用户的工单", description = "按创建时间倒序返回当前登录用户有权查看的未删除工单")
    public List<WorkOrder> list() {
        return service.listCurrentUserWorkOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询工单详情", description = "仅返回当前登录用户有权查看的指定工单")
    public WorkOrder get(@Parameter(description = "工单ID", required = true, example = "1") @PathVariable long id) {
        return service.getCurrentUserWorkOrder(id);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "查询工单对话历史", description = "返回当前用户工单最近 20 条持久化消息")
    public List<SupportMessage> messages(@PathVariable long id) {
        return conversationService.history(id);
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "关闭工单", description = "关闭当前用户的工单并清理对应的多轮对话记忆")
    public WorkOrder close(@Parameter(description = "工单ID", required = true, example = "1") @PathVariable long id) {
        return service.close(id);
    }
}
