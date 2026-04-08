package com.scaffold.bizlog.component;

import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    /**
     * 编辑用户信息，记录变更前后数据
     */
    @LogRecord(
            bizNo = "{{#user.userId}}",
            success = "编辑用户信息，用户ID：{{#user.userId}}，修改前用户名：{{#oldUser.username}}，修改后用户名：{{#user.username}}，修改前手机号：{{#oldUser.phone}}，修改后手机号：{{#user.phone}}",
            type = "user"
    )
    public boolean updateUser(UserInfo user) {
        // 查询数据库旧数据
        UserInfo oldUser = getUserById(user.getUserId());
        log.info("执行用户更新操作，用户ID：{}", user.getUserId());
        // 旧数据存入上下文，供日志注解读取
        LogRecordContext.putVariable("oldUser", oldUser);
        return true;
    }

    /**
     * 删除用户，记录删除操作
     */
    @LogRecord(
            bizNo = "{{#userId}}",
            success = "删除用户，用户ID：{{#userId}}",
            type = "user"
    )
    public boolean deleteUser(Long userId) {
        log.info("执行用户删除操作，用户ID：{}", userId);
        return true;
    }

    /**
     * 模拟根据ID查询用户旧数据
     */
    private UserInfo getUserById(Long userId) {
        UserInfo oldUser = new UserInfo();
        oldUser.setUserId(userId);
        oldUser.setUsername("测试用户");
        oldUser.setPhone("13800138000");
        oldUser.setAge(20);
        return oldUser;
    }
}