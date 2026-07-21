package com.scaffold.support.assistant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/examples/support/assistant")
@RequiredArgsConstructor
@Tag(name = "售后订单助手", description = "使用只读 Tool Calling 查询当前用户的订单、物流和商品")
public class SupportAssistantController {

    private final SupportAssistantService service;

    @PostMapping("/chat")
    @Operation(summary = "咨询订单和物流", description = "仅暴露 query_order、query_logistics 和 query_product 三个只读工具")
    public ChatResponse chat(@RequestBody @Valid ChatRequest request) {
        return new ChatResponse(service.chat(request));
    }

}
