package com.scaffold.ai;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommonAiTools {

    @Tool(
        name = "addNumbers",
        description = "计算两个整数之和"
    )
    public int addNumbers(
            @ToolParam(description = "第一个整数") int left,
            @ToolParam(description = "第二个整数") int right) {
        return left + right;
    }

    @Tool(
        name = "currentTime",
        description = "获取服务器当前日期和时间"
    )
    public String currentTime() {
        return LocalDateTime.now().toString();
    }
}