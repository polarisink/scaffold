package com.scaffold.support.refund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 退款确认或取消请求，令牌只能由准备接口生成。
 */
public record ConfirmRefundRequest(
        @NotBlank(message = "确认标识不能为空")
        @Pattern(regexp = "[A-Za-z0-9_-]{40,64}", message = "确认标识格式不正确")
        String confirmationId) {
}
