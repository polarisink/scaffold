package com.scaffold.security.util;

import com.scaffold.core.base.util.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 响应工具类
 */
public class ResponseUtil {
    /**
     * 响应写入json对象
     *
     * @param response 响应
     * @param body     body
     * @throws IOException 异常
     */
    public static void writeBody(HttpServletResponse response, Object body) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String json = body instanceof String ? (String) body : JsonUtil.toJson(body);
        response.getWriter().write(json);
    }
}
