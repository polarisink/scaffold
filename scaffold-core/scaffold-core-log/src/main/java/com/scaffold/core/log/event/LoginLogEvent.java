package com.scaffold.core.log.event;

import com.scaffold.core.log.vo.BusinessStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录事件
 *
 * @author Lion Li
 */

@Data
public class LoginLogEvent implements Serializable {

    private Long userId;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 登录状态 0成功 1失败
     */
    private Integer status = BusinessStatus.SUCCESS.ordinal();
}
