package github.polarisink.dao.bean.auth;

import static github.polarisink.common.constant.AuthConst.SUPER_ADMIN_ROLE_ID;

import java.util.Objects;
import lombok.Data;

/**
 * Authentication
 *
 * @author aries
 * @since 2022-06-21
 */
@Data
public class Authentication {

  private Long uid;
  private Long roleId;

  /**
   * 是不是超级管理员
   *
   * @return
   */
  public boolean isSuperAdmin() {
    return Objects.equals(this.roleId, SUPER_ADMIN_ROLE_ID);
  }
}
