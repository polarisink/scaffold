package com.scaffold.support;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "售后工单创建请求")
public record CreateWorkOrderRequest(
        @Schema(description = "客户端生成的幂等请求标识；同一用户重复提交相同标识时返回已有工单",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "request_0001")
        String requestId,
        @Schema(description = "用户提交的自然语言售后描述", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "手机无法开机，订单号202607190001，我想申请退款")
        String description) {
}