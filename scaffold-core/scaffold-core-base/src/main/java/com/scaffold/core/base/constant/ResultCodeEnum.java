package com.scaffold.core.base.constant;

import com.scaffold.core.base.exception.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ResultCodeEnum implements Assert {

    /**
     * 成功返回
     */
    SUCCESS("成功"),

    /**
     * 服务器繁忙，请稍后重试
     * 无法识别的异常，尽可能对通过判断减少未定义异常抛出
     */
    SERVER_ERROR("服务器或网络开小差了，请联系管理员"),

    SERVER_ERROR_CUSTOM("{0}"),

    SERVER_RESTARTING("系统正在准备中，请稍后重试"),

    SERVER_BUSY("系统繁忙中，请稍后重试"),
    ACTION_FAIL("操作失败，请稍后重试!"),
    NC_CONNECTION_POOL_ERROR("系统数据库连接异常，请联系管理员"),

    /**
     * 绑定参数校验异常
     */
    PARAMETER_VALID_ERROR("参数校验异常"),
    INPUT_ERROR("输入错误,请检查输入数据!!!"),

    /**
     * 重复提交
     */
    REPETITIVE_OPERATION_ERROR("重复性操作"),

    /**
     * token过期
     */
    TOKEN_EXPIRED("token过期"),

    /**
     * token过期
     */
    TOKEN_UNAUTHORIZED("错误的token"),

    /**
     * token过期
     */
    TOKEN_FORBIDDEN("请携带token访问受保护的资源"),
    /**
     * 登录错误
     */
    USER_OR_PASS_ERROR("用户名或密码错误"),
    /**
     * 未经授权
     */
    UNAUTHORIZED("请登录后重试！"),

    /**
     * 没有权限
     */
    NO_OPERATOR_AUTH("无权限操作！"),

    /**
     * token过期
     */
    ERROR("{0}"),

    /**
     * 未登录
     */
    NOT_LOGIN("非常抱歉,请登录后再试!"),

    /**
     * IP黑名单，禁止访问
     */
    FORRBIDDEN_ACCESS_ERROR("IP黑名单，禁止访问"),

    /**
     * 限流访问
     */
    RATELIMIT_ACCESS_ERROR("请求人数过载, 请稍后访问"),

    /**
     * 导入
     */
    NO_FILE("文件不存在"),
    NO_SIZE("文件大小不能超过10M!"),
    NO_FORMAT("文件名格式不正确"),
    FILE_INPUT_ERROR("导入文件失败,{0}"),
    FILE_READ_ERROR("文件读取失败,{0}"),

    DOWNLOAD_ERROR("下载文件失败 请联系管理员"),
    /* --------   常用空指针判断   -------- */
    LOGIN_EXCEPTION("登录异常,请重试!"),
    BEGIN_AFTER_END("开始时间异常,不能再结束时间之后"),
    ENTITY_NOT_EXISTS("实体不存在"),
    ;


    /**
     * 返回消息
     */
    private final String message;
}
