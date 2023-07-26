package github.polarisink.scaffold.infrastructure.asserts;

import lombok.Getter;
import lombok.ToString;

/**
 * 通用业务异常尽可能通用，如空异常
 *
 * @author aries
 * @date 2022/5/20
 */
@Getter
@ToString
public enum BusinessE implements BaseEnum {
    //模板相关

    USED_TEMPLATE_NAME("该模板名已被使用:{}"),
    EMPTY_TEMPLATE("模板{}为空，请先添加步骤！"),
    UNBALANCED_STEP("模板{}结构不平衡，请重新编辑！"),
    NO_STRUCT_OR_DATA_NODE("模板{}没有结构头或数据头，请添加！"),
    NOT_EQUALS_DEPTH("模板{}的层数和非数据表头长度不匹配！"),
    UNIQUE_TEMPLATE_HEADER("该模板中之前存在名为{}的文件头！"),

    //机床相关
    NO_TEMPLATE_IN_MODEL("机床型号没有任何模板,请先添加！"),
    UNIQUE_MACH_NUM("机床编号不能重复"),
    UNIQUE_SN("SN码不能重复"),
    UNIQUE_BOM("bom名不能重复"),

    USED_TEMPLATE("该型号不能被删除,已被部分机床档案使用:{}"),


    //验证码相关
    EXPIRED_CODE("验证码已过期,请重新发送"),
    ERROR_CODE("验证码不正确,请重新发送"),
    SEND_SMS_ERROR("短信发送失败"),

    //用户相关

    HAS_CHILD_MENU("该菜单下有子菜单,请先删除子菜单"),
    PARENT_MUST_BE_DIR("上级只能为目录类型"),
    EMPTY_URL("菜单URL不能为空"),
    UNIQUE_ROLE_NAME("角色名重复"),
    CANNOT_EDIT_SUPER_ADMIN("非超级管理员不能修改超级管理员用户"),
    ROLE_HAS_USER("有用户绑定该角色[{}],现在不能删除"),
    USED_PHONE_NUM("该手机号已被使用"),
    INVALID_OLD_PASSWORD("旧密码不正确"),
    PLEASE_LOGIN("请先登录"),
    USER_NOT_EXISTS("不存在手机号为[{}]的用户"),
    NO_DATA_FORM("没有数据表单");


    /**
     * 返回码
     */
    private final int code;
    /**
     * 返回消息
     */
    private final String message;

    BusinessE(String errorMsg) {
        this.code = AssertConst.BUSINESS_ERROR_CODE;
        this.message = errorMsg;

    }
}
