package com.scaffold.web.config;

import com.scaffold.base.util.ServletUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static com.scaffold.base.constant.GlobalConstant.TRACE_ID;

/**
 * 传递traceId
 */
@Configuration
public class TraceIdClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String traceId = ServletUtils.getHeader(TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            request.getHeaders().add(TRACE_ID, traceId);
        }
        return execution.execute(request, body);
    }
}
