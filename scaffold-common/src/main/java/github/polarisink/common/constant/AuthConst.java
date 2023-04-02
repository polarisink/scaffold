package github.polarisink.common.constant;

import cn.hutool.core.util.StrUtil;

/**
 * 鉴权相关常量
 *
 * @author aries
 * @date 2022/6/23
 */
public interface AuthConst {

  /**
   * 携带token的header的名字
   */
   String AUTH_HEADER = "Authorization";

  /**
   * token前缀
   */
   String TOKEN_PREFIX = "Bearer";


  /**
   * 临时设置的token过期时间设置为30分钟
   */
   int EXPIRATION_MINUTES = 30;

  /**
   * todo 开发为了方便使用较长时间
   * 短信验证码过期时间(分钟)
   */
   long VERIFY_CODE_TIMEOUT = 30;

   long LONG_TIME = 1000;

  /**
   * 超级管理员ID
   */
   long SUPER_ADMIN_ROLE_ID = 0L;

  /**
   * 根菜单的父id
   */
   long ROOT_MENU_PID = 0L;

  /**
   * 权限分割符,必须为英文符号逗号
   */
   String PERM_SEP = StrUtil.COMMA;

  /**
   * 权限管理菜单的id,超级管理员之外的的不能看见
   */
   long AUTH_MENU_ID = 15L;


  /**
   * 密钥,不能泄露
   */
   String SECRET = "78be79caee544f58937016b2690f16d2";

  /**
   * token过期时间
   */
   int EXPIRATION_DAYS = 3;


}
