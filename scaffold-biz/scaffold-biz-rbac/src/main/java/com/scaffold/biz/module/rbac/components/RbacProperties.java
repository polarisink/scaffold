package com.scaffold.biz.module.rbac.components;

import com.scaffold.core.base.constant.GlobalConstant;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * 密码重置策略
 */
enum ResetStrategy {
    /**
     * 直接使用用户名
     */
    USERNAME,
    /**
     * 使用时间戳
     */
    TIMESTAMP,
    /**
     * 用户名加时间戳
     */
    USERNAME_AND_TIMESTAMP

}

/**
 * rbac配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rbac")
public class RbacProperties implements InitializingBean {
    /**
     * 重置策略
     */
    private ResetStrategy reset = ResetStrategy.USERNAME;
    /**
     * 时间戳类型
     */
    private String pattern = GlobalConstant.DEFAULT_DATE_FORMAT;
    /**
     * 间隔符
     */
    private String separator = "";
    /**
     * 用户名是否在前面
     */
    private Boolean usernameBehind = true;

    @Override
    public void afterPropertiesSet() {
        //如果不是用户名，就要校验时间戳
        if (reset != ResetStrategy.USERNAME) {
            String regex = "^[yMdHmsS\\-/:\\s]+$";
            Assert.state(pattern != null && pattern.matches(regex), "不合法的时间格式");
        }
    }
}
