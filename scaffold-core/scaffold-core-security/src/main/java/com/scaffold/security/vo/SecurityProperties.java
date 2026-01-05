package com.scaffold.security.vo;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限设置
 */
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private static final String[] DEFAULT_IGNORE_LIST = {"/auth/*", "/scenario/detail/*", "/favicon.ico", "/error", "/v3/api-docs/swagger-config", "/v3/api-docs/*", "/swagger-ui/**", "/*/v3/api-docs/**", "/swagger-ui.html", "/doc.html", "/webjars/**"};
    private String[] ignoreList;

    //默认的和配置文件合并一起忽略
    public String[] getIgnoreList() {
        if (ignoreList == null || ignoreList.length == 0) {
            return DEFAULT_IGNORE_LIST;
        }
        String[] res = new String[DEFAULT_IGNORE_LIST.length + this.ignoreList.length];
        System.arraycopy(DEFAULT_IGNORE_LIST, 0, res, 0, DEFAULT_IGNORE_LIST.length);
        System.arraycopy(this.ignoreList, 0, res, DEFAULT_IGNORE_LIST.length, this.ignoreList.length);
        return res;
    }
}
