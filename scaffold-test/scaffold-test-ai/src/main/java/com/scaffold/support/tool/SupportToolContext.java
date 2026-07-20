package com.scaffold.support.tool;

import org.springframework.ai.chat.model.ToolContext;

/** Keys and typed accessors for context values supplied by trusted server code. */
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
