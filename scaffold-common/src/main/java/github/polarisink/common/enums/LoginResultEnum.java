package github.polarisink.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录结果的枚举类
 *
 * @author hzsk
 */
@Getter
@AllArgsConstructor
public enum LoginResultEnum implements BaseEnum {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 账号或密码不正确
     */
    BAD_CREDENTIALS(10, "账号或密码不正确"),
    /**
     * 用户被禁用
     */
    USER_DISABLED(20, "用户被禁用"),
    /**
     * token不存在或过期
     */
    TOKEN_TIMEOUT(30, "Token不存在或过期"),
    /**
     * 图片验证码不存在
     */
    //CAPTCHA_NOT_FOUND(30,"图片验证码不存在"),
    /**
     * 图片验证码不正确
     */
    //CAPTCHA_CODE_ERROR(31,"图片验证码不正确"),

    /**
     * 未知异常
     */
    //UNKNOWN_ERROR(100,"未知异常"),
    ;

    /**
     * 结果
     */
    private final Integer type;
    private final String name;

}
