package com.scaffold.support.identity;

/**
 * 提供经过认证的当前用户 ID，禁止从模型输出或请求参数中读取用户身份。
 */
public interface SupportCurrentUserProvider {

    long requireUserId();
}
