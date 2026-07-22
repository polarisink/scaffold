package com.scaffold.support.assistant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供绑定工单和当前登录用户的售后多轮对话接口。
 */
@RestController
@RequestMapping("/api/examples/support/assistant")
@RequiredArgsConstructor
@Tag(name = "售后订单助手", description = "查询售后信息，并可创建需要用户二次确认的退款准备操作")
public class SupportAssistantController {

    private final SupportAssistantService service;

    /**
     * 发送一轮工单消息并返回助手生成的回复。
     */
    @PostMapping("/chat")
    @Operation(summary = "咨询订单和物流", description = "查询工具只读；prepare_refund 只创建待确认操作，不能执行退款")
    public ChatResponse chat(@RequestBody @Valid ChatRequest request) {
        return new ChatResponse(service.chat(request));
    }

}
