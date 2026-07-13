package com.scaffold.web.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        // 先把当前线程的 MDC 上下文 copy 一份，别直接用引用
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                // 异步线程里，把 copy 的上下文恢复回去
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                // 执行异步任务（比如发通知、算积分）
                runnable.run();
            } finally {
                // 任务结束，清掉异步线程的 MDC，避免串线
                MDC.clear();
            }
        };
    }
}
