package com.scaffold.security.util;

import com.scaffold.base.util.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {
    public static void writeBody(HttpServletResponse response, Object body) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String json = body instanceof String ? (String) body : JsonUtil.toJson(body);
        response.getWriter().write(json);
    }
}
