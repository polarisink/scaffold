package com.scaffold.support.workorder;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 创建售后工单的已校验请求，requestId 用于当前用户范围内的幂等控制。
 */
@Schema(description = "售后工单创建请求")
public record CreateWorkOrderRequest(
        @Schema(description = "客户端生成的幂等请求标识；同一用户重复提交相同标识时返回已有工单",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "request_0001")
        @NotBlank(message = "请求标识不能为空")
        @Pattern(regexp = "[A-Za-z0-9_-]{8,100}", message = "请求标识格式不正确")
        String requestId,
        @Schema(description = "用户提交的自然语言售后描述", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "手机无法开机，订单号202607190001，我想申请退款")
        @NotBlank(message = "售后描述不能为空")
        @Size(max = 4_000, message = "售后描述不能超过4000个字符")
        String description) {
}
