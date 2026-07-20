package com.scaffold.support;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "工单意图分析请求")
public record AnalyzeRequest(
        @NotBlank(message = "会话标识不能为空")
        @Schema(description = "会话标识；仅用于演示阶段的对话记忆",
                example = "support-42")
        String conversationId,

        @Size(min = 0, max = 4000, message = "消息长度不能超过4000")
        @Schema(description = "用户提交的自然语言售后描述", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "手机无法开机，订单号202607190001，我想申请退款")
        String message) {
}