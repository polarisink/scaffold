package github.polarisink.scaffold.infrastructure.asserts;

import lombok.Getter;

/**
 * 参数异常
 *
 * @author aries
 * @date 2022/5/20
 */
@Getter
public enum AuthE implements BaseEnum {

    NOT_LOGIN("未登录"),

    USER_NOT_EXISTS("不存在该用户,请联系管理员添加！"),
    EMPTY_ROLE("您的角色没有任何权限,请先联系管理员"),

    INVALID_USERNAME_OR_PASSWORD("账号或密码不正确,请重新输入"),
    USER_BANED("您已经被封禁,请联系管理员");

    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String message;

    AuthE(String message) {
        this.code = AssertConst.AUTH_ERROR_CODE;
        this.message = message;
    }
}
