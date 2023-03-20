package github.polarisink.common.constant;

import cn.hutool.core.util.StrUtil;

/**
 * 鉴权相关常量
 *
 * @author aries
 * @date 2022/6/23
 */
public class AuthConst {

    /**
     * 携带token的header的名字
     */
    public static final String AUTH_HEADER = "Authorization";

    /**
     * token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer";


    /**
     * 临时设置的token过期时间设置为30分钟
     */
    public static final int EXPIRATION_MINUTES = 30;

    /**
     * todo 开发为了方便使用较长时间
     * 短信验证码过期时间(分钟)
     */
    public static final long VERIFY_CODE_TIMEOUT = 30;

    public static final long LONG_TIME = 1000;

    /**
     * 超级管理员ID
     */
    public static final long SUPER_ADMIN_ROLE_ID = 0L;

    /**
     * 根菜单的父id
     */
    public static final long ROOT_MENU_PID = 0L;

    /**
     * 权限分割符,必须为英文符号逗号
     */
    public static final String PERM_SEP = StrUtil.COMMA;

    /**
     * 权限管理菜单的id,超级管理员之外的的不能看见
     */
    public static final long AUTH_MENU_ID = 15L;


    /**
     * 密钥,不能泄露
     */
    public static final String SECRET = "78be79caee544f58937016b2690f16d2";

    /**
     * token过期时间
     */
    public static final int EXPIRATION_DAYS = 3;

    private AuthConst() {
    }

}
