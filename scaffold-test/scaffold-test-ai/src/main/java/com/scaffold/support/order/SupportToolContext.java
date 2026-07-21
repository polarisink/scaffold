package com.scaffold.support.order;

import org.springframework.ai.chat.model.ToolContext;

/** 服务端可信工具上下文的键定义及类型安全读取方法。 */
public final class SupportToolContext {

    public static final String USER_ID = "support.userId";
    public static final String REQUEST_ID = "support.requestId";

    private SupportToolContext() {
    }

    public static long requireLong(ToolContext context, String key) {
        if (context == null) {
            throw new IllegalArgumentException("Missing tool context");
        }
        Object value = context.getContext().get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalArgumentException("Missing trusted tool context value: " + key);
    }

    public static String requireString(ToolContext context, String key) {
        if (context == null) {
            throw new IllegalArgumentException("Missing tool context");
        }
        Object value = context.getContext().get(key);
        if (value instanceof String text && !text.isBlank()) {
            return text;
        }
        throw new IllegalArgumentException("Missing trusted tool context value: " + key);
    }
}
