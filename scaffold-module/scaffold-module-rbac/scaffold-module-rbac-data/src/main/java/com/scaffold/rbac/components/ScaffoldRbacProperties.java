package com.scaffold.rbac.components;

import com.scaffold.base.constant.GlobalConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
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
 *
 * @param reset             重置策略
 * @param pattern           重置时间戳格式
 * @param separator         分隔符
 * @param usernameBehind    用户名在前面还是后面
 * @param anonymousUsername 匿名用户的名字
 * @param logEnabled        是否记录日志
 */
@ConfigurationProperties(prefix = "scaffold.rbac")
public record ScaffoldRbacProperties(
        @DefaultValue("USERNAME") ResetStrategy reset,
        @DefaultValue(GlobalConstant.DEFAULT_DATE_FORMAT) String pattern,
        @DefaultValue("") String separator,
        @DefaultValue("true") Boolean usernameBehind,
        @DefaultValue("anonymous") String anonymousUsername,
        @DefaultValue("true") Boolean logEnabled) {
    public ScaffoldRbacProperties {
        // 如果不是用户名，就要校验时间戳
        if (reset != ResetStrategy.USERNAME) {
            String regex = "^[yMdHmsS\\-/:\\s]+$";
            Assert.state(pattern != null && pattern.matches(regex), "不合法的时间格式");
        }
    }
}
