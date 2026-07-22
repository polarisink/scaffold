package com.scaffold.support.assistant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 工单多轮对话请求，工单归属由服务端结合当前登录用户校验。
 */
public record ChatRequest(
        @NotNull(message = "工单ID不能为空") @Positive(message = "工单ID必须为正数")
        @Schema(description = "当前用户的工单ID", example = "42") Long workOrderId,
        @NotBlank(message = "消息不能为空")
        @Size(max = 4000, message = "消息长度不能超过4000个字符")
        @Schema(description = "订单或物流问题", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "订单202607190001的物流到哪里了") String message) {
}
