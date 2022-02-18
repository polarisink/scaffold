package com.lqs.scaffold.log;


import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 打印restTemplate远程请求配置
 *
 * @author aries
 * @date 2022/02/17
 */
@Slf4j(topic = "outgoing")
public class LogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public @NotNull ClientHttpResponse intercept(HttpRequest request, byte @NotNull [] body, ClientHttpRequestExecution execution) throws IOException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ClientHttpResponse response = execution.execute(request, body);
        stopWatch.stop();
        StringBuilder resBody = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(),
                StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                resBody.append(line);
                line = bufferedReader.readLine();
            }
        }
        //当然图片、文件一类的就可以省了，打出日志没啥用处，此处的业务逻辑随意撸了，比如header头信息类似于  Accept 、Accept-Encoding 、Accept-Language、Connection 等等
        if (request.getHeaders().getContentType() != null && request.getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA)) {
            body = new byte[]{};
        }
        log.info("Request URL    : {}", request.getURI());
        log.info("HTTP Method    : {}", request.getMethodValue());
        log.info("Headers        : {}", request.getHeaders() + "ms");
        log.info("Cost-Time      : {}", stopWatch.getLastTaskTimeMillis());
        log.info("Status         : {}", response.getRawStatusCode());
        log.info("Body           : {}", new String(body, StandardCharsets.UTF_8));
        return response;
    }

    @Data
    @Builder
    @SuppressWarnings("rawtypes")
    private static class RestLog {
        private String reqUrl;
        private String method;
        private HttpHeaders headers;
        private String reqBody;
        private String resBody;
        private long costTime;
        private int resStatus;
    }
}
