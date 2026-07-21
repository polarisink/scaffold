package com.scaffold.support.order;

import com.scaffold.support.order.model.LogisticsSummary;
import com.scaffold.support.order.model.OrderSummary;
import com.scaffold.support.order.model.ProductSummary;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 暴露给 Spring AI 的只读订单工具；用户 ID 和请求 ID 必须来自可信 ToolContext。
 */
@Component
public class SupportOrderTools {

    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("\\d{12,32}");

    private final OrderService orderService;

    public SupportOrderTools(OrderService orderService) {
        this.orderService = orderService;
    }

    @Tool(name = "query_order", description = """
            查询当前登录用户自己的订单摘要。仅当用户明确提供订单号时调用；
            不得猜测、补全或生成订单号。
            """)
    public OrderSummary queryOrder(
            @ToolParam(description = "用户明确提供的12到32位数字订单号") String orderNo,
            ToolContext context) {
        return orderService.queryOrder(normalizeOrderNo(orderNo), requireUserId(context));
    }

    @Tool(name = "query_logistics", description = """
            根据用户明确提供的订单号，查询当前登录用户自己的订单物流摘要；
            不得猜测、补全或生成订单号。
            """)
    public LogisticsSummary queryLogistics(
            @ToolParam(description = "用户明确提供的12到32位数字订单号") String orderNo,
            ToolContext context) {
        return orderService.queryLogistics(normalizeOrderNo(orderNo), requireUserId(context));
    }

    @Tool(name = "query_product", description = """
            根据用户明确提供的订单号，查询当前登录用户所购商品的售后摘要；
            不得使用任意商品ID枚举商品信息。
            """)
    public ProductSummary queryProduct(
            @ToolParam(description = "用户明确提供的12到32位数字订单号") String orderNo,
            ToolContext context) {
        return orderService.queryProduct(normalizeOrderNo(orderNo), requireUserId(context));
    }

    private static long requireUserId(ToolContext context) {
        SupportToolContext.requireString(context, SupportToolContext.REQUEST_ID);
        return SupportToolContext.requireLong(context, SupportToolContext.USER_ID);
    }

    private static String normalizeOrderNo(String orderNo) {
        String normalized = orderNo == null ? "" : orderNo.trim();
        if (!ORDER_NUMBER_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("orderNo must contain 12 to 32 digits");
        }
        return normalized;
    }
}
