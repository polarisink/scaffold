package com.scaffold.support.refund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 创建退款待确认操作的请求。
 */
public record PrepareRefundRequest(
        @NotBlank(message = "订单号不能为空")
        @Pattern(regexp = "\\d{12,32}", message = "订单号必须为12到32位数字")
        String orderNo,
        @NotBlank(message = "退款原因不能为空")
        @Size(max = 500, message = "退款原因不能超过500个字符")
        String reason) {
}
