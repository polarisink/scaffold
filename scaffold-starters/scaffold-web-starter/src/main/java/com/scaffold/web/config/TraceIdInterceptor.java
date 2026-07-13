package com.scaffold.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static com.scaffold.base.constant.GlobalConstant.TRACE_ID;

@Component
public class TraceIdInterceptor implements HandlerInterceptor {
    // 统一用 "traceId" 当key，和网关保持一致，不然拿不到
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头拿网关传的 traceId（现在几乎不会为空，除非绕开网关调用）
        String traceId = request.getHeader(TRACE_ID);
        // 防止极端情况（比如本地测试没走网关），还是加个判断，为空就生成一个
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        // 塞到 MDC 里，后面所有日志都能拿到
        MDC.put(TRACE_ID, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 特别重要！请求结束后一定要清掉 MDC，不然线程池复用会导致 traceId 串了
        // 比如线程A处理请求1，没清 MDC，再处理请求2时，会带着请求1的 traceId
        MDC.remove(TRACE_ID);
    }
}
