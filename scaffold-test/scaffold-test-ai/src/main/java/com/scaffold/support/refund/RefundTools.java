package com.scaffold.support.refund;

import com.scaffold.support.order.SupportToolContext;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 仅向模型暴露退款准备能力，确认退款绝不能注册为 AI 工具。
 */
@Component
@Validated
public class RefundTools {

    private final RefundService refundService;

    public RefundTools(RefundService refundService) {
        this.refundService = refundService;
    }

    @Tool(name = "prepare_refund", description = """
            为当前登录用户准备退款确认卡片，但不会执行退款。
            只有用户明确要求退款并给出订单号和原因时才能调用。
            返回的 confirmationId 必须交给用户通过普通确认接口二次确认。
            """)
    public PendingAction prepareRefund(
            @ToolParam(description = "用户明确提供的12到32位数字订单号")
            @Pattern(regexp = "\\d{12,32}", message = "订单号必须为12到32位数字") String orderNo,
            @ToolParam(description = "用户明确说明的退款原因，不得由模型编造")
            @NotBlank(message = "退款原因不能为空") @Size(max = 500, message = "退款原因不能超过500个字符") String reason,
            ToolContext context) {
        SupportToolContext.requireLong(context, SupportToolContext.USER_ID);
        SupportToolContext.requireString(context, SupportToolContext.REQUEST_ID);
        return refundService.prepare(new PrepareRefundRequest(orderNo, reason));
    }
}
