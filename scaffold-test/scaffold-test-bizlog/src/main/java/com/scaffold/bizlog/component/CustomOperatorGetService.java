package com.scaffold.bizlog.component;

import com.mzt.logapi.beans.Operator;
import com.mzt.logapi.service.IOperatorGetService;
import org.springframework.stereotype.Component;

@Component
public class CustomOperatorGetService implements IOperatorGetService {

    @Override
    public Operator getUser() {
        // 替换为项目真实获取登录用户逻辑
        // 示例：return UserContext.getCurrentUser().getUsername();
        // 测试阶段可暂用固定值，上线后对接登录体系
        return new Operator("admin");
    }
}